package ru.my.scents.mcp;

import java.io.PrintStream;

final class SafeToolLogger {
    private static final int MAX_DETAIL_LENGTH = 120;
    private final PrintStream stderr;

    SafeToolLogger(PrintStream stderr) {
        this.stderr = stderr;
    }

    void call(String tool, String detail) {
        stderr.printf("TOOL_CALL tool=%s %s%n", safe(tool), safe(detail));
    }

    void result(String tool, String status, String detail) {
        stderr.printf("TOOL_RESULT tool=%s status=%s %s%n", safe(tool), safe(status), safe(detail));
    }

    private String safe(String value) {
        if (value == null) {
            return "unknown";
        }
        String escaped = value.replace("\r", "\\r").replace("\n", "\\n");
        return escaped.length() <= MAX_DETAIL_LENGTH ? escaped : escaped.substring(0, MAX_DETAIL_LENGTH) + "...";
    }
}
