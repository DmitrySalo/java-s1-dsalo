package ru.my.scents.mcp;

import static org.assertj.core.api.Assertions.assertThat;

import io.modelcontextprotocol.json.McpJsonDefaults;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.condition.DisabledIfSystemProperty;
import org.junit.jupiter.api.Test;

class StdioServerSmokeTest {
    private static final Duration READ_TIMEOUT = Duration.ofSeconds(60);

    @Test
    @DisabledIfSystemProperty(named = "mcp.skip.stdio.smoke", matches = "true")
    void keepsDiagnosticsOutOfJsonRpcStdout() throws Exception {
        ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/d", "/s", "/c", launcher().toString())
                .directory(Path.of("").toAbsolutePath().toFile());
        builder.environment().put("JAVA_HOME", Path.of(System.getProperty("java.home")).toString());
        builder.environment().put("MCP_PROJECT_ROOT", Path.of("").toAbsolutePath().getParent().toString());
        Process process = builder.start();
        List<String> stderr = new ArrayList<>();
        Thread stderrReader = new Thread(() -> drain(process.getErrorStream(), stderr), "mcp-server-stderr");
        stderrReader.start();
        ExecutorService stdoutReader = Executors.newSingleThreadExecutor();
        try (BufferedWriter stdin = new BufferedWriter(new OutputStreamWriter(process.getOutputStream(), StandardCharsets.UTF_8));
                BufferedReader stdout = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
            send(stdin, "{\"jsonrpc\":\"2.0\",\"id\":1,\"method\":\"initialize\",\"params\":{\"protocolVersion\":\"2025-03-26\",\"capabilities\":{},\"clientInfo\":{\"name\":\"mcp-smoke-test\",\"version\":\"1\"}}}");
            assertJsonRpcResponse(readJson(stdout, stdoutReader), 1);
            send(stdin, "{\"jsonrpc\":\"2.0\",\"method\":\"notifications/initialized\",\"params\":{}}");
            send(stdin, "{\"jsonrpc\":\"2.0\",\"id\":2,\"method\":\"tools/list\",\"params\":{}}");
            Map<String, Object> tools = readJson(stdout, stdoutReader);
            assertJsonRpcResponse(tools, 2);
            assertThat(tools.toString()).contains("get_local_documentation", "search_project", "run_project_check")
                    .doesNotContain("TOOL_CALL", "TOOL_RESULT");
            assertThat(tools.toString()).contains("minLength=2", "maxLength=120", "maxLength=200",
                    "minimum=1", "maximum=50", "mcp_tests", "additionalProperties=false");
            callTool(stdin, stdout, stdoutReader, 3, "get_local_documentation", "{\"query\":\"local tool safety\"}");
            callTool(stdin, stdout, stdoutReader, 4, "get_local_documentation", "{\"query\":\"stdio\"}");
            callTool(stdin, stdout, stdoutReader, 5, "search_project", "{\"query\":\"ToolResult\",\"path_prefix\":\"mcp-server/src\",\"max_results\":50}");
            callTool(stdin, stdout, stdoutReader, 7, "search_project", "{\"query\":\"SafeToolLogger\",\"path_prefix\":\"mcp-server/src\",\"max_results\":50}");
            assertToolInputRejected(callTool(stdin, stdout, stdoutReader, 8, "get_local_documentation", "{\"query\":\"x\"}"), "get_local_documentation");
            assertToolInputRejected(callTool(stdin, stdout, stdoutReader, 9, "get_local_documentation", "{\"query\":\"stdio\",\"extra\":true}"), "get_local_documentation");
            assertToolInputRejected(callTool(stdin, stdout, stdoutReader, 10, "search_project", "{\"query\":42}"), "search_project");
            assertToolInputRejected(callTool(stdin, stdout, stdoutReader, 11, "search_project", "{\"query\":\"safe\",\"path_prefix\":\"x" + "x".repeat(201) + "\"}"), "search_project");
            assertToolInputRejected(callTool(stdin, stdout, stdoutReader, 12, "run_project_check", "{\"check\":\"unknown\"}"), "run_project_check");
            assertToolInputRejected(callToolWithoutArguments(stdin, stdout, stdoutReader, 13, "get_local_documentation"), "get_local_documentation");
            assertToolInputRejected(callToolWithoutArguments(stdin, stdout, stdoutReader, 14, "search_project"), "search_project");
            assertToolInputRejected(callToolWithoutArguments(stdin, stdout, stdoutReader, 15, "run_project_check"), "run_project_check");
            assertToolInputRejected(callTool(stdin, stdout, stdoutReader, 16, "get_local_documentation", "{}"), "get_local_documentation");
            assertToolInputRejected(callTool(stdin, stdout, stdoutReader, 17, "search_project", "{}"), "search_project");
            assertToolInputRejected(callTool(stdin, stdout, stdoutReader, 18, "run_project_check", "{}"), "run_project_check");
            assertToolInputRejected(callTool(stdin, stdout, stdoutReader, 19, "search_project", "{\"query\":{\"nested\":[\"value\"]}}"), "search_project");
            callTool(stdin, stdout, stdoutReader, 20, "get_local_documentation", "{\"query\":\"stdio\"}");
        } finally {
            process.destroy();
            process.waitFor(READ_TIMEOUT.toMillis(), TimeUnit.MILLISECONDS);
            if (process.isAlive()) {
                process.destroyForcibly();
            }
            stderrReader.join(READ_TIMEOUT.toMillis());
            stdoutReader.shutdownNow();
        }
        assertThat(stderr).contains(
                "TOOL_CALL tool=get_local_documentation query_length=17",
                "TOOL_CALL tool=get_local_documentation query_length=5",
                "TOOL_CALL tool=search_project query_length=14",
                    "TOOL_CALL tool=search_project query_length=10",
                    "TOOL_RESULT tool=get_local_documentation status=success result=ok",
                    "TOOL_RESULT tool=search_project status=success result=ok");
    }

    private static Path launcher() {
        return Path.of("build", "install", "mcp-server", "bin", "mcp-server.bat").toAbsolutePath();
    }

    private static void send(BufferedWriter stdin, String message) throws IOException {
        stdin.write(message);
        stdin.newLine();
        stdin.flush();
    }

    private static Map<String, Object> callTool(BufferedWriter stdin, BufferedReader stdout, ExecutorService stdoutReader, int id, String name, String arguments) throws Exception {
        send(stdin, "{\"jsonrpc\":\"2.0\",\"id\":" + id + ",\"method\":\"tools/call\",\"params\":{\"name\":\"" + name + "\",\"arguments\":" + arguments + "}}");
        Map<String, Object> response = readJson(stdout, stdoutReader);
        assertJsonRpcResponse(response, id);
        return response;
    }

    private static Map<String, Object> callToolWithoutArguments(BufferedWriter stdin, BufferedReader stdout, ExecutorService stdoutReader, int id, String name) throws Exception {
        send(stdin, "{\"jsonrpc\":\"2.0\",\"id\":" + id + ",\"method\":\"tools/call\",\"params\":{\"name\":\"" + name + "\"}}");
        Map<String, Object> response = readJson(stdout, stdoutReader);
        assertJsonRpcResponse(response, id);
        return response;
    }

    @SuppressWarnings("unchecked")
    private static void assertToolInputRejected(Map<String, Object> response, String tool) {
        Map<String, Object> result = (Map<String, Object>) response.get("result");
        assertThat(result).containsEntry("isError", true);
        Map<String, Object> structuredContent = (Map<String, Object>) result.get("structuredContent");
        Map<String, Object> error = (Map<String, Object>) structuredContent.get("error");
        assertThat(structuredContent).containsEntry("status", "error").containsEntry("tool", tool);
        assertThat(error).containsEntry("code", "INVALID_ARGUMENT")
                .containsEntry("message", "Tool arguments do not match the input schema.")
                .doesNotContainKeys("stack_trace", "path", "command");
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> readJson(BufferedReader stdout, ExecutorService stdoutReader) throws Exception {
        Future<String> lineFuture = stdoutReader.submit(stdout::readLine);
        String line = lineFuture.get(READ_TIMEOUT.toMillis(), TimeUnit.MILLISECONDS);
        assertThat(line).isNotNull();
        return McpJsonDefaults.getMapper().readValue(line, Map.class);
    }

    private static void assertJsonRpcResponse(Map<String, Object> response, int id) {
        assertThat(response).containsEntry("jsonrpc", "2.0").containsEntry("id", id).containsKey("result");
    }

    private static void drain(java.io.InputStream stream, List<String> records) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            for (String line; (line = reader.readLine()) != null;) {
                records.add(line);
            }
        } catch (IOException ignored) {
            // The process is terminated by the test after all assertions complete.
        }
    }
}
