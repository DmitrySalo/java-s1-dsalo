package ru.my.scents.mcp;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Map;
import org.junit.jupiter.api.Test;

class ToolResultAndLoggerTest {
    @Test
    void usesStructuredSuccessAndErrorContracts() {
        ToolResult success = ToolResult.success("search_project", "Found matches.", Map.of("matches", 1));
        ToolResult failure = ToolResult.failure("search_project", "ACCESS_DENIED", "Requested project path is not available.");

        assertThat(success.error()).isFalse();
        assertThat(success.structuredContent()).containsEntry("status", "success").containsEntry("tool", "search_project");
        assertThat(failure.error()).isTrue();
        assertThat(failure.structuredContent()).containsEntry("status", "error").containsEntry("tool", "search_project");
        assertThat(failure.structuredContent().toString()).contains("ACCESS_DENIED");
    }

    @Test
    void writesOnlySafeCallAndResultRecords() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        SafeToolLogger logger = new SafeToolLogger(new PrintStream(output));

        logger.call("search_project", "query_length=4");
        logger.result("search_project", "success", "result=ok");

        assertThat(output.toString()).contains("TOOL_CALL tool=search_project query_length=4")
                .contains("TOOL_RESULT tool=search_project status=success result=ok");
    }

    @Test
    void escapesLineBreaksInDiagnosticRecords() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        SafeToolLogger logger = new SafeToolLogger(new PrintStream(output));

        logger.call("run_project_check", "check=mcp_tests\r\nFORGED");

        assertThat(output.toString()).contains("check=mcp_tests\\r\\nFORGED")
                .doesNotContain("check=mcp_tests\r\nFORGED");
    }
}
