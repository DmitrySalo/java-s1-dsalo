---
name: mcp-server-review
description: Use when reviewing a Java MCP server or MCP client configuration for protocol correctness, sandbox escape, unsafe command execution, secret exposure, and missing tests.
---

# MCP Server Review

Read `.opencode/instructions/mcp.md`, `.opencode/instructions/mcp-security.md`, and `.opencode/instructions/mcp-testing.md`. Inspect the diff and relevant context before reporting only actionable findings.

Prioritize protocol corruption from `stdout` logs, permissive schemas, missing structured error results, filesystem sandbox bypasses, symlink handling, unrestricted subprocess execution, excessive outputs, secret exposure, and absent negative tests. State residual IDE acceptance gaps explicitly when no findings remain.
