---
name: mcp-server-development
description: Use when implementing or configuring a local Java Model Context Protocol server, stdio transport, tools, IDE integration, or evidence of tool calls.
---

# MCP Server Development

Read `.opencode/instructions/mcp.md`. For a local server, prefer the official MCP Java SDK and `stdio` transport unless the task explicitly requires another transport. Inspect the current SDK documentation and selected client configuration before coding.

Keep the server separate from `my-scents`. Define tools with JSON Schema input validation and structured results. Preserve `stdout` exclusively for the protocol and write safe diagnostics only to `stderr`. Load `mcp-security` for filesystem, process, environment or logging work and `mcp-testing` for tests and acceptance evidence.
