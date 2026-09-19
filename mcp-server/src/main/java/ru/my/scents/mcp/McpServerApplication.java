package ru.my.scents.mcp;

import io.modelcontextprotocol.json.McpJsonDefaults;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.McpSyncServer;
import io.modelcontextprotocol.server.transport.StdioServerTransportProvider;
import io.modelcontextprotocol.spec.McpSchema.CallToolResult;
import io.modelcontextprotocol.spec.McpSchema.JsonSchema;
import io.modelcontextprotocol.spec.McpSchema.Tool;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;

/** Starts a local stdio MCP server. stdout is reserved for MCP JSON-RPC. */
public final class McpServerApplication {
    private McpServerApplication() {
    }

    public static void main(String[] args) throws Exception {
        Path moduleRoot = Path.of("").toAbsolutePath();
        Path repositoryRoot = projectRoot(moduleRoot);
        SafeToolLogger logger = new SafeToolLogger(System.err);
        DocumentationTool docs = new DocumentationTool(moduleRoot.resolve("docs"));
        ProjectSearchTool search = new ProjectSearchTool(new ProjectFileAccessPolicy(repositoryRoot));
        ProjectCheckTool checks = new ProjectCheckTool(repositoryRoot);
        StdioServerTransportProvider transport = new StdioServerTransportProvider(McpJsonDefaults.getMapper());
        McpSyncServer server = McpServer.sync(transport)
                .serverInfo("my-scents-local-mcp", "0.1.0")
                .tool(tool(DocumentationTool.NAME, "Search prepared local MCP documentation.", docsSchema()),
                        (exchange, arguments) -> toMcp(call(logger, DocumentationTool.NAME, "query_length=" + length(argument(arguments, "query")),
                                executeDocumentation(docs, arguments))))
                .tool(tool(ProjectSearchTool.NAME, "Search allowed text files inside the trusted project root.", searchSchema()),
                        (exchange, arguments) -> toMcp(call(logger, ProjectSearchTool.NAME, "query_length=" + length(argument(arguments, "query")),
                                executeSearch(search, arguments))))
                .tool(tool(ProjectCheckTool.NAME, "Run one allowlisted local project check.", checkSchema()),
                        (exchange, arguments) -> toMcp(call(logger, ProjectCheckTool.NAME, checkDetail(string(argument(arguments, "check"))),
                                executeCheck(checks, arguments))))
                .build();
        Runtime.getRuntime().addShutdownHook(new Thread(server::close));
    }

    private static Path projectRoot(Path moduleRoot) {
        String configured = System.getenv("MCP_PROJECT_ROOT");
        return configured == null || configured.isBlank() ? moduleRoot.getParent() : Path.of(configured);
    }

    private static Tool tool(String name, String description, JsonSchema schema) {
        return Tool.builder().name(name).description(description).inputSchema(schema).build();
    }

    private static ToolResult call(SafeToolLogger logger, String tool, String detail, ToolResult result) {
        logger.call(tool, detail);
        logger.result(tool, result.error() ? "error" : "success", result.error() ? "code=error" : "result=ok");
        return result;
    }

    private static CallToolResult toMcp(ToolResult result) {
        return CallToolResult.builder().addTextContent(result.summary()).structuredContent(result.structuredContent()).isError(result.error()).build();
    }

    private static JsonSchema docsSchema() {
        return new JsonSchema("object", Map.of("query", Map.of("type", "string", "minLength", 2, "maxLength", 120)),
                java.util.List.of("query"), false, null, null);
    }
    private static JsonSchema searchSchema() {
        return new JsonSchema("object", Map.of("query", Map.of("type", "string", "minLength", 1, "maxLength", 120), "path_prefix", Map.of("type", "string", "maxLength", 200), "max_results", Map.of("type", "integer", "minimum", 1, "maximum", 50)), java.util.List.of("query"), false, null, null);
    }
    private static JsonSchema checkSchema() {
        return new JsonSchema("object", Map.of("check", Map.of("type", "string", "enum", java.util.List.of("mcp_tests"))), java.util.List.of("check"), false, null, null);
    }
    private static ToolResult executeDocumentation(DocumentationTool tool, Map<String, Object> arguments) {
        if (!validString(arguments, "query", 2, 120, Set.of("query"))) {
            return ToolResult.failure(DocumentationTool.NAME, "INVALID_ARGUMENT", "Tool arguments do not match the input schema.");
        }
        return tool.execute(string(arguments.get("query")));
    }

    private static ToolResult executeSearch(ProjectSearchTool tool, Map<String, Object> arguments) {
        Set<String> fields = Set.of("query", "path_prefix", "max_results");
        if (!validString(arguments, "query", 1, 120, fields)
                || (arguments != null && arguments.containsKey("path_prefix") && !validString(arguments, "path_prefix", 0, 200, fields))
                || (arguments != null && arguments.containsKey("max_results") && !validMaxResults(arguments, fields))) {
            return ToolResult.failure(ProjectSearchTool.NAME, "INVALID_ARGUMENT", "Tool arguments do not match the input schema.");
        }
        return tool.execute(string(arguments.get("query")), string(arguments.get("path_prefix")), (Integer) arguments.get("max_results"));
    }

    static ToolResult executeCheck(ProjectCheckTool tool, Map<String, Object> arguments) {
        if (!validString(arguments, "check", 0, 0, Set.of("check")) || !"mcp_tests".equals(arguments.get("check"))) {
            return ToolResult.failure(ProjectCheckTool.NAME, "INVALID_ARGUMENT", "Tool arguments do not match the input schema.");
        }
        return tool.execute(string(arguments.get("check")));
    }

    private static boolean validMaxResults(Map<String, Object> arguments, Set<String> fields) {
        return !hasUnexpectedFields(arguments, fields) && arguments.get("max_results") instanceof Integer value && value >= 1 && value <= 50;
    }

    private static boolean validString(Map<String, Object> arguments, String name, int minLength, int maxLength, Set<String> fields) {
        return !hasUnexpectedFields(arguments, fields) && arguments.get(name) instanceof String value
                && value.length() >= minLength && (maxLength == 0 || value.length() <= maxLength);
    }

    private static boolean hasUnexpectedFields(Map<String, Object> arguments, Set<String> fields) {
        return arguments == null || !fields.containsAll(arguments.keySet());
    }

    private static Object argument(Map<String, Object> arguments, String name) { return arguments == null ? null : arguments.get(name); }
    private static String string(Object value) { return value instanceof String string ? string : null; }
    private static int length(Object value) { return value instanceof String string ? string.length() : 0; }
    private static String checkDetail(String check) { return "check=" + ("mcp_tests".equals(check) ? "mcp_tests" : "invalid"); }
}
