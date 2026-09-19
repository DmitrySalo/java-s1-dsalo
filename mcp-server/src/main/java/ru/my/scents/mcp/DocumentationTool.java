package ru.my.scents.mcp;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

final class DocumentationTool {
    static final String NAME = "get_local_documentation";
    private static final int MAX_MATCHES = 10;
    private static final int MAX_VISITED_ENTRIES = 2_000;
    private static final int MAX_INDEXED_LINES = 10_000;
    private static final int MAX_INDEXED_BYTES = 4 * 1024 * 1024;
    private final Path documentationRoot;
    private final ProjectFileAccessPolicy policy;
    private final List<IndexedLine> index;
    private final boolean indexTruncated;
    private final boolean unavailable;

    DocumentationTool(Path documentationRoot) {
        this.documentationRoot = documentationRoot;
        ProjectFileAccessPolicy accessPolicy;
        try {
            accessPolicy = new ProjectFileAccessPolicy(documentationRoot);
        } catch (IOException exception) {
            this.policy = null;
            this.index = List.of();
            this.indexTruncated = true;
            this.unavailable = true;
            return;
        }
        this.policy = accessPolicy;
        Snapshot snapshot = buildIndex();
        this.index = snapshot.lines();
        this.indexTruncated = snapshot.truncated();
        this.unavailable = snapshot.unavailable();
    }

    ToolResult execute(String query) {
        if (query == null || query.trim().length() < 2 || query.length() > 120) {
            return ToolResult.failure(NAME, "INVALID_ARGUMENT", "Query must contain 2 to 120 characters.");
        }
        if (unavailable) {
            return ToolResult.failure(NAME, "DOCUMENTATION_UNAVAILABLE", "Local documentation is unavailable.");
        }
        String normalizedQuery = query.toLowerCase(Locale.ROOT);
        List<Map<String, Object>> matches = new ArrayList<>();
        boolean truncated = indexTruncated;
        for (IndexedLine line : index) {
            if (!line.text().toLowerCase(Locale.ROOT).contains(normalizedQuery)
                    && !line.path().toLowerCase(Locale.ROOT).contains(normalizedQuery)) {
                continue;
            }
            matches.add(match(line.path(), line.line()));
            if (matches.size() == MAX_MATCHES) {
                truncated = true;
                break;
            }
        }
        return ToolResult.success(NAME, matches.isEmpty() ? "No documentation matches found." : "Documentation matches found.",
                Map.of("query_length", query.length(), "matches", matches, "truncated", truncated));
    }

    private Snapshot buildIndex() {
        List<IndexedLine> lines = new ArrayList<>();
        boolean truncated = false;
        int indexedBytes = 0;
        try (var files = Files.walk(documentationRoot, 4)) {
            var iterator = files.iterator();
            for (int visited = 0; iterator.hasNext();) {
                if (visited == MAX_VISITED_ENTRIES) {
                    truncated = true;
                    break;
                }
                visited++;
                Path file = iterator.next();
                if (!file.toString().endsWith(".md") || !policy.isReadableTextFile(file)) {
                    continue;
                }
                try {
                    List<String> fileLines = policy.readAllowedTextFile(file);
                    String path = documentationRoot.relativize(file).toString().replace('\\', '/');
                    for (int index = 0; index < fileLines.size(); index++) {
                        String line = fileLines.get(index);
                        int lineBytes = line.getBytes(StandardCharsets.UTF_8).length;
                        if (lines.size() == MAX_INDEXED_LINES || indexedBytes + lineBytes > MAX_INDEXED_BYTES) {
                            return new Snapshot(List.copyOf(lines), true, false);
                        }
                        lines.add(new IndexedLine(path, index + 1, line));
                        indexedBytes += lineBytes;
                    }
                } catch (ProjectFileAccessPolicy.AccessDeniedException ignored) {
                    // Ignore files that cannot be safely snapshotted during server startup.
                }
            }
        } catch (IOException | UncheckedIOException | SecurityException exception) {
            return new Snapshot(List.of(), true, true);
        }
        return new Snapshot(List.copyOf(lines), truncated, false);
    }

    private Map<String, Object> match(String path, int line) {
        Map<String, Object> match = new LinkedHashMap<>();
        match.put("path", path);
        match.put("start_line", line);
        match.put("end_line", line);
        return match;
    }

    private record IndexedLine(String path, int line, String text) {
    }

    private record Snapshot(List<IndexedLine> lines, boolean truncated, boolean unavailable) {
    }
}
