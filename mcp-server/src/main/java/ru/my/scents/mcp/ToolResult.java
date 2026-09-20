package ru.my.scents.mcp;

import java.util.LinkedHashMap;
import java.util.Map;

record ToolResult(boolean error, String summary, Map<String, Object> structuredContent) {

    static ToolResult success(String tool, String summary, Map<String, Object> data) {
        return new ToolResult(false, summary, Map.of("status", "success", "tool", tool, "data", data));
    }

    static ToolResult failure(String tool, String code, String message) {
        Map<String, Object> content = new LinkedHashMap<>();
        content.put("status", "error");
        content.put("tool", tool);
        content.put("error", Map.of("code", code, "message", message));
        return new ToolResult(true, message, Map.copyOf(content));
    }
}
