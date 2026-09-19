package ru.my.scents.mcp;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.FileVisitResult;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

final class ProjectSearchTool {
    static final String NAME = "search_project";
    private static final int DEFAULT_MAX_RESULTS = 20;
    private static final int MAX_RESULTS = 50;
    private static final int MAX_VISITED_ENTRIES = 2_000;
    private static final int MAX_PATH_LENGTH = 240;
    private static final int MAX_INDEXED_LINES = 10_000;
    private static final int MAX_INDEXED_BYTES = 4 * 1024 * 1024;
    private final ProjectFileAccessPolicy policy;
    private final List<IndexedLine> index;
    private final Set<String> directories;
    private final Set<String> deniedPaths;
    private final boolean indexTruncated;

    ProjectSearchTool(ProjectFileAccessPolicy policy) {
        this.policy = policy;
        Snapshot snapshot = buildIndex();
        this.index = snapshot.lines();
        this.directories = snapshot.directories();
        this.deniedPaths = snapshot.deniedPaths();
        this.indexTruncated = snapshot.truncated();
    }

    ToolResult execute(String query, String pathPrefix, Integer maxResults) {
        if (query == null || query.isBlank() || query.length() > 120) {
            return ToolResult.failure(NAME, "INVALID_ARGUMENT", "Query must contain 1 to 120 characters.");
        }
        int limit = maxResults == null ? DEFAULT_MAX_RESULTS : maxResults;
        if (limit < 1 || limit > MAX_RESULTS) {
            return ToolResult.failure(NAME, "INVALID_ARGUMENT", "max_results must be between 1 and 50.");
        }
        if (indexTruncated && index.isEmpty()) {
            return ToolResult.failure(NAME, "SEARCH_UNAVAILABLE", "Project search is unavailable.");
        }
        Path searchRoot = policy.root();
        try {
            if (pathPrefix != null && !pathPrefix.isBlank()) {
                String normalizedPrefix = policy.normalizeRelativePath(pathPrefix);
                if (deniedPaths.contains(normalizedPrefix)) {
                    return ToolResult.failure(NAME, "ACCESS_DENIED", "Requested project path is not available.");
                }
                if (!directories.contains(normalizedPrefix)) {
                    return ToolResult.failure(NAME, "INVALID_ARGUMENT", "path_prefix must identify an allowed directory.");
                }
                searchRoot = policy.root().resolve(normalizedPrefix);
            }
        } catch (ProjectFileAccessPolicy.AccessDeniedException exception) {
            return ToolResult.failure(NAME, "ACCESS_DENIED", "Requested project path is not available.");
        }

        List<Map<String, Object>> matches = new ArrayList<>();
        boolean[] truncated = {indexTruncated};
        String relativeRoot = policy.relative(searchRoot);
        for (IndexedLine line : index) {
            if (!withinPrefix(line.path(), relativeRoot) || !line.text().contains(query)) {
                continue;
            }
            if (matches.size() == limit) {
                truncated[0] = true;
                break;
            }
            matches.add(match(line.path(), line.line()));
        }
        return ToolResult.success(NAME, matches.isEmpty() ? "No project matches found." : "Project matches found.",
                Map.of("query_length", query.length(), "matches", matches, "truncated", truncated[0]));
    }

    private Snapshot buildIndex() {
        List<IndexedLine> lines = new ArrayList<>();
        Set<String> directories = new java.util.HashSet<>();
        Set<String> deniedPaths = new java.util.HashSet<>();
        directories.add("");
        boolean[] truncated = {false};
        int[] indexedBytes = {0};
        int[] visited = {0};
        try {
            Files.walkFileTree(policy.root(), java.util.Set.of(), ProjectFileAccessPolicy.MAX_DEPTH, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult preVisitDirectory(Path directory, BasicFileAttributes attributes) {
                if (visited[0]++ == MAX_VISITED_ENTRIES) {
                    truncated[0] = true;
                    return FileVisitResult.TERMINATE;
                }
                String path = policy.relative(directory);
                if (!path.isEmpty() && (!policy.isAllowedDirectory(directory) || Files.isSymbolicLink(directory))) {
                    deniedPaths.add(path);
                    return FileVisitResult.SKIP_SUBTREE;
                }
                directories.add(path);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) throws IOException {
                if (visited[0]++ == MAX_VISITED_ENTRIES) {
                    truncated[0] = true;
                    return FileVisitResult.TERMINATE;
                }
                String path = policy.relative(file);
                if (Files.isSymbolicLink(file)) {
                    deniedPaths.add(path);
                    return FileVisitResult.CONTINUE;
                }
                if (!policy.isReadableTextFile(file)) {
                    return FileVisitResult.CONTINUE;
                }
                try {
                    List<String> fileLines = policy.readAllowedTextFile(file);
                    for (int index = 0; index < fileLines.size(); index++) {
                        String line = fileLines.get(index);
                        int lineBytes = line.getBytes(StandardCharsets.UTF_8).length;
                        if (lines.size() == MAX_INDEXED_LINES || indexedBytes[0] + lineBytes > MAX_INDEXED_BYTES) {
                            truncated[0] = true;
                            return FileVisitResult.TERMINATE;
                        }
                        lines.add(new IndexedLine(path, index + 1, line));
                        indexedBytes[0] += lineBytes;
                    }
                } catch (ProjectFileAccessPolicy.AccessDeniedException ignored) {
                    // Ignore files that cannot be safely snapshotted during server startup.
                }
                return FileVisitResult.CONTINUE;
            }
            });
        } catch (IOException | UncheckedIOException | SecurityException exception) {
            return new Snapshot(List.of(), Set.of(""), Set.of(), true);
        }
        return new Snapshot(List.copyOf(lines), Set.copyOf(directories), Set.copyOf(deniedPaths), truncated[0]);
    }

    private static boolean withinPrefix(String path, String prefix) {
        return prefix.isEmpty() || path.startsWith(prefix + "/");
    }

    private Map<String, Object> match(String path, int line) {
        Map<String, Object> match = new LinkedHashMap<>();
        match.put("path", path.length() <= MAX_PATH_LENGTH ? path : path.substring(0, MAX_PATH_LENGTH) + "...");
        match.put("line", line);
        return match;
    }

    private record IndexedLine(String path, int line, String text) {
    }

    private record Snapshot(List<IndexedLine> lines, Set<String> directories, Set<String> deniedPaths, boolean truncated) {
    }
}
