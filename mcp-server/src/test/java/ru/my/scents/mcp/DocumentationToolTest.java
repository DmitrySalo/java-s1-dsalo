package ru.my.scents.mcp;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class DocumentationToolTest {
    @TempDir Path docs;

    @Test
    void findsDocumentationText() throws Exception {
        Files.writeString(docs.resolve("safety.md"), "# Tool safety\nChecks use a static allowlist.");
        ToolResult result = new DocumentationTool(docs).execute("allowlist");
        assertThat(result.error()).isFalse();
        assertThat(result.structuredContent().toString()).contains("safety.md");
    }

    @Test
    void doesNotReturnQueryOrMatchedDocumentationText() throws Exception {
        String secret = "token=not-for-client";
        Files.writeString(docs.resolve("safety.md"), secret);

        ToolResult result = new DocumentationTool(docs).execute(secret);

        assertThat(result.error()).isFalse();
        assertThat(result.structuredContent().toString()).contains("safety.md", "query_length")
                .doesNotContain(secret);
    }

    @Test
    void truncatesAfterVisitingTheEntryLimit() throws Exception {
        for (int index = 0; index < 2_001; index++) {
            Files.writeString(docs.resolve("doc-" + index + ".txt"), "unrelated text");
        }

        ToolResult result = new DocumentationTool(docs).execute("absent");

        assertThat(result.error()).isFalse();
        assertThat(result.structuredContent().toString()).contains("truncated=true");
    }

    @Test
    void usesStartupSnapshotInsteadOfOpeningFilesDuringToolCall() throws Exception {
        Path document = docs.resolve("safety.md");
        Files.writeString(document, "before-startup");
        DocumentationTool tool = new DocumentationTool(docs);
        Files.writeString(document, "after-startup");

        ToolResult original = tool.execute("before-startup");
        ToolResult replacement = tool.execute("after-startup");

        assertThat(original.structuredContent().toString()).contains("safety.md");
        assertThat(replacement.structuredContent().toString()).doesNotContain("safety.md");
    }

    @Test
    void truncatesSnapshotAtTheAggregateLineLimit() throws Exception {
        Files.writeString(docs.resolve("many-lines.md"), "xx\n".repeat(10_001));

        ToolResult result = new DocumentationTool(docs).execute("xx");

        assertThat(result.error()).isFalse();
        assertThat(result.structuredContent().toString()).contains("truncated=true");
    }
}
