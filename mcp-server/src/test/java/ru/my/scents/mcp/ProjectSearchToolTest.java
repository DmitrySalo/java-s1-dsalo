package ru.my.scents.mcp;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ProjectSearchToolTest {
    @TempDir Path root;

    @Test
    void returnsProjectRelativeMatchesAndSkipsEnvFiles() throws Exception {
        Files.writeString(root.resolve("Example.java"), "class Example { String target = \"find-me\"; }");
        Files.writeString(root.resolve(".env"), "SECRET=find-me");
        ToolResult result = new ProjectSearchTool(new ProjectFileAccessPolicy(root)).execute("find-me", null, 20);
        assertThat(result.error()).isFalse();
        assertThat(result.structuredContent().toString()).contains("Example.java").doesNotContain("SECRET");
    }

    @Test
    void deniesTraversal() throws Exception {
        ToolResult result = new ProjectSearchTool(new ProjectFileAccessPolicy(root)).execute("x", "../", 20);
        assertThat(result.structuredContent().toString()).contains("ACCESS_DENIED");
    }

    @Test
    void deniesAbsolutePathPrefix() throws Exception {
        ToolResult result = new ProjectSearchTool(new ProjectFileAccessPolicy(root))
                .execute("x", root.toAbsolutePath().toString(), 20);

        assertThat(result.error()).isTrue();
        assertThat(result.structuredContent().toString()).contains("ACCESS_DENIED");
    }

    @Test
    void deniesPlatformInvalidPathPrefix() throws Exception {
        ToolResult result = new ProjectSearchTool(new ProjectFileAccessPolicy(root)).execute("x", "bad\u0000path", 20);

        assertThat(result.error()).isTrue();
        assertThat(result.structuredContent().toString()).contains("ACCESS_DENIED");
    }

    @Test
    void deniesBlockedDirectory() throws Exception {
        Files.createDirectory(root.resolve(".git"));
        ToolResult result = new ProjectSearchTool(new ProjectFileAccessPolicy(root)).execute("x", ".git", 20);

        assertThat(result.error()).isTrue();
        assertThat(result.structuredContent().toString()).contains("ACCESS_DENIED");
    }

    @Test
    void ignoresBinaryAndOversizedFiles() throws Exception {
        Files.write(root.resolve("binary.java"), new byte[] {0, 1, 2});
        Files.writeString(root.resolve("large.java"), "x".repeat((int) ProjectFileAccessPolicy.MAX_FILE_BYTES + 1));

        ToolResult result = new ProjectSearchTool(new ProjectFileAccessPolicy(root)).execute("x", null, 20);

        assertThat(result.error()).isFalse();
        assertThat(result.structuredContent().toString()).doesNotContain("binary.java", "large.java");
    }

    @Test
    void ignoresTextFilesWithSensitiveNames() throws Exception {
        Files.writeString(root.resolve("credentials.txt"), "token=find-me");
        Files.writeString(root.resolve("private-key.txt"), "key-material=find-me");
        Files.writeString(root.resolve("private.key.txt"), "key-material=find-me");
        Files.writeString(root.resolve("apiKey.txt"), "key-material=find-me");
        Files.writeString(root.resolve("id-rsa.txt"), "key-material=find-me");
        Files.writeString(root.resolve("private key.txt"), "key-material=find-me");
        Files.writeString(root.resolve("api key.txt"), "key-material=find-me");
        Files.writeString(root.resolve("id rsa.txt"), "key-material=find-me");
        Files.createDirectory(root.resolve(".ssh"));
        Files.writeString(root.resolve(".ssh").resolve("config.txt"), "host=find-me");
        Files.writeString(root.resolve("application.java"), "String value = \"find-me\";");

        ToolResult result = new ProjectSearchTool(new ProjectFileAccessPolicy(root)).execute("find-me", null, 20);

        assertThat(result.error()).isFalse();
        assertThat(result.structuredContent().toString()).contains("application.java")
                .doesNotContain("credentials.txt", "private-key.txt", "private.key.txt", "apiKey.txt", "id-rsa.txt", "private key.txt", "api key.txt", "id rsa.txt", ".ssh", "token=", "key-material=");
    }

    @Test
    void usesStartupSnapshotInsteadOfOpeningFilesDuringToolCall() throws Exception {
        Path source = root.resolve("application.java");
        Files.writeString(source, "class Example { String value = \"before-startup\"; }");
        ProjectSearchTool tool = new ProjectSearchTool(new ProjectFileAccessPolicy(root));
        Files.writeString(source, "class Example { String value = \"after-startup\"; }");

        ToolResult original = tool.execute("before-startup", null, 20);
        ToolResult replacement = tool.execute("after-startup", null, 20);

        assertThat(original.structuredContent().toString()).contains("application.java");
        assertThat(replacement.structuredContent().toString()).doesNotContain("application.java");
    }

    @Test
    void truncatesSnapshotAtTheAggregateLineLimit() throws Exception {
        Files.writeString(root.resolve("many-lines.java"), "x\n".repeat(10_001));

        ToolResult result = new ProjectSearchTool(new ProjectFileAccessPolicy(root)).execute("x", null, 20);

        assertThat(result.error()).isFalse();
        assertThat(result.structuredContent().toString()).contains("truncated=true");
    }

    @Test
    void searchesOnlyTheBoundedPrefixOfEachLine() throws Exception {
        Files.writeString(root.resolve("long-line.java"), "x".repeat(ProjectFileAccessPolicy.MAX_LINE_LENGTH) + "find-me");

        ToolResult result = new ProjectSearchTool(new ProjectFileAccessPolicy(root)).execute("find-me", null, 20);

        assertThat(result.error()).isFalse();
        assertThat(result.structuredContent().toString()).doesNotContain("long-line.java");
    }

    @Test
    void doesNotReturnQueryOrMatchedSourceText() throws Exception {
        String secret = "token=not-for-client";
        Files.writeString(root.resolve("application.java"), "String value = \"" + secret + "\";");

        ToolResult result = new ProjectSearchTool(new ProjectFileAccessPolicy(root)).execute(secret, null, 20);

        assertThat(result.error()).isFalse();
        assertThat(result.structuredContent().toString()).contains("application.java", "query_length")
                .doesNotContain(secret);
    }

    @Test
    void ignoresSymbolicLinks() throws Exception {
        Path outside = Files.createTempFile("mcp-search-outside", ".java");
        Files.writeString(outside, "outside-secret-match");
        Path linkedFile = root.resolve("linked.java");
        Path linkedDirectory = root.resolve("linked-directory");
        try {
            Files.createSymbolicLink(linkedFile, outside);
            Files.createSymbolicLink(linkedDirectory, outside.getParent());
        } catch (UnsupportedOperationException | java.nio.file.FileSystemException exception) {
            return;
        }

        ToolResult fileResult = new ProjectSearchTool(new ProjectFileAccessPolicy(root))
                .execute("outside-secret-match", null, 20);
        ToolResult directoryResult = new ProjectSearchTool(new ProjectFileAccessPolicy(root))
                .execute("outside-secret-match", "linked-directory", 20);

        assertThat(fileResult.error()).isFalse();
        assertThat(fileResult.structuredContent().toString()).doesNotContain("linked.java", "matches=[{");
        assertThat(directoryResult.error()).isTrue();
        assertThat(directoryResult.structuredContent().toString()).contains("ACCESS_DENIED");
    }

    @Test
    void truncatesAfterVisitingTheEntryLimit() throws Exception {
        for (int index = 0; index < 2_000; index++) {
            Files.writeString(root.resolve("file-" + index + ".java"), "class Example {}");
        }

        ToolResult result = new ProjectSearchTool(new ProjectFileAccessPolicy(root)).execute("absent", null, 20);

        assertThat(result.error()).isFalse();
        assertThat(result.structuredContent().toString()).contains("truncated=true");
    }
}
