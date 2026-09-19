package ru.my.scents.mcp;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

final class ProjectFileAccessPolicy {
    static final long MAX_FILE_BYTES = 128 * 1024;
    static final int MAX_LINE_LENGTH = 500;
    static final int MAX_DEPTH = 12;

    private static final Set<String> BLOCKED_NAMES = Set.of(
            ".git", ".idea", ".gradle", "build", "node_modules", ".venv", ".env", ".ssh");
    private static final Set<String> BLOCKED_EXTENSIONS = Set.of(
            ".pem", ".key", ".p12", ".pfx", ".jks", ".keystore", ".credentials");
    private static final Set<String> BLOCKED_NAME_PARTS = Set.of(
            "credential", "secret", "password", "token", "privatekey", "apikey", "idrsa");
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            ".java", ".py", ".md", ".gradle", ".proto", ".txt");

    private final Path root;

    ProjectFileAccessPolicy(Path configuredRoot) throws IOException {
        this.root = configuredRoot.toRealPath();
    }

    Path root() {
        return root;
    }

    Path resolveExisting(String relativePath) throws IOException, AccessDeniedException {
        if (relativePath == null || relativePath.isBlank()) {
            throw new AccessDeniedException();
        }
        Path supplied;
        try {
            supplied = Path.of(relativePath);
        } catch (InvalidPathException exception) {
            throw new AccessDeniedException();
        }
        if (supplied.isAbsolute() || supplied.normalize().startsWith("..")) {
            throw new AccessDeniedException();
        }
        Path candidate = root.resolve(supplied).normalize();
        if (!candidate.startsWith(root) || containsBlockedSegment(candidate)) {
            throw new AccessDeniedException();
        }
        Path realPath = candidate.toRealPath();
        if (!realPath.startsWith(root) || Files.isSymbolicLink(candidate)) {
            throw new AccessDeniedException();
        }
        return realPath;
    }

    String normalizeRelativePath(String relativePath) throws AccessDeniedException {
        if (relativePath == null || relativePath.isBlank()) {
            throw new AccessDeniedException();
        }
        Path supplied;
        try {
            supplied = Path.of(relativePath);
        } catch (InvalidPathException exception) {
            throw new AccessDeniedException();
        }
        if (supplied.isAbsolute() || supplied.normalize().startsWith("..")) {
            throw new AccessDeniedException();
        }
        Path candidate = root.resolve(supplied).normalize();
        if (!candidate.startsWith(root) || containsBlockedSegment(candidate)) {
            throw new AccessDeniedException();
        }
        return root.relativize(candidate).toString().replace('\\', '/');
    }

    boolean isReadableTextFile(Path file) {
        try {
            if (!Files.isRegularFile(file, LinkOption.NOFOLLOW_LINKS)
                    || Files.isSymbolicLink(file)
                    || Files.size(file) > MAX_FILE_BYTES
                    || containsBlockedSegment(file)
                    || !ALLOWED_EXTENSIONS.contains(extension(file))) {
                return false;
            }
            return !containsNulByte(file);
        } catch (IOException exception) {
            return false;
        }
    }

    List<String> readAllowedTextFile(Path file) throws IOException, AccessDeniedException {
        Path realPath = allowedRegularFile(file);
        byte[] bytes = new byte[(int) MAX_FILE_BYTES + 1];
        int total = 0;
        // NOFOLLOW_LINKS prevents a final-component symlink swap after validation.
        try (SeekableByteChannel channel = Files.newByteChannel(file, StandardOpenOption.READ, LinkOption.NOFOLLOW_LINKS)) {
            for (int read; (read = channel.read(ByteBuffer.wrap(bytes, total, bytes.length - total))) > 0;) {
                total += read;
                if (total > MAX_FILE_BYTES) {
                    throw new AccessDeniedException();
                }
            }
        }
        if (!realPath.equals(file.toRealPath()) || !allowedRegularFile(file).equals(realPath)) {
            throw new AccessDeniedException();
        }
        for (int index = 0; index < total; index++) {
            if (bytes[index] == 0) {
                throw new AccessDeniedException();
            }
        }
        return splitLines(new String(bytes, 0, total, StandardCharsets.UTF_8));
    }

    boolean isAllowedDirectory(Path directory) {
        try {
            Path realPath = directory.toRealPath();
            return realPath.startsWith(root) && !Files.isSymbolicLink(directory) && !containsBlockedSegment(realPath);
        } catch (IOException exception) {
            return false;
        }
    }

    String relative(Path path) {
        return root.relativize(path).toString().replace('\\', '/');
    }

    private Path allowedRegularFile(Path file) throws IOException, AccessDeniedException {
        if (!Files.isRegularFile(file, LinkOption.NOFOLLOW_LINKS) || Files.isSymbolicLink(file)
                || Files.size(file) > MAX_FILE_BYTES || containsBlockedSegment(file)
                || !ALLOWED_EXTENSIONS.contains(extension(file))) {
            throw new AccessDeniedException();
        }
        Path realPath = file.toRealPath();
        if (!realPath.startsWith(root)) {
            throw new AccessDeniedException();
        }
        return realPath;
    }

    private boolean containsNulByte(Path file) throws IOException {
        try (SeekableByteChannel channel = Files.newByteChannel(file, StandardOpenOption.READ, LinkOption.NOFOLLOW_LINKS)) {
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            int total = 0;
            while (channel.read(buffer) > 0) {
                total += buffer.position();
                if (total > MAX_FILE_BYTES) {
                    return true;
                }
                buffer.flip();
                while (buffer.hasRemaining()) {
                    if (buffer.get() == 0) {
                        return true;
                    }
                }
                buffer.clear();
            }
            return false;
        }
    }

    private List<String> splitLines(String content) {
        List<String> lines = new ArrayList<>();
        int start = 0;
        for (int index = 0; index < content.length(); index++) {
            if (content.charAt(index) == '\n') {
                lines.add(linePrefix(content, start, index));
                start = index + 1;
            }
        }
        if (start < content.length()) {
            lines.add(linePrefix(content, start, content.length()));
        }
        return lines;
    }

    private String linePrefix(String content, int start, int end) {
        int boundedEnd = Math.min(end, start + MAX_LINE_LENGTH);
        return content.substring(start, boundedEnd).replace("\r", "");
    }

    private boolean containsBlockedSegment(Path path) {
        for (Path segment : path) {
            String value = segment.toString().toLowerCase(Locale.ROOT);
            String normalized = normalizeName(value);
            if (BLOCKED_NAMES.contains(value) || value.startsWith(".env") || BLOCKED_EXTENSIONS.contains(extension(value))
                    || BLOCKED_NAME_PARTS.stream().anyMatch(normalized::contains)) {
                return true;
            }
        }
        return false;
    }

    private String normalizeName(String value) {
        StringBuilder normalized = new StringBuilder(value.length());
        value.codePoints().filter(Character::isLetterOrDigit).forEach(normalized::appendCodePoint);
        return normalized.toString();
    }

    private String extension(Path path) {
        return extension(path.getFileName().toString());
    }

    private String extension(String name) {
        int separator = name.lastIndexOf('.');
        return separator < 0 ? "" : name.substring(separator).toLowerCase(Locale.ROOT);
    }

    static final class AccessDeniedException extends Exception {
    }
}
