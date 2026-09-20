---
name: mcp-security
description: Use when MCP tools read project files, execute local checks, process untrusted input, use environment variables, or emit logs.
---

# MCP Security

Read `.opencode/instructions/mcp-security.md` before editing. Treat tool arguments as untrusted. Enforce canonical-path containment within a trusted root, deny sensitive and generated paths, and bound all file operations.

For process tools use a static allowlist and `ProcessBuilder` argument list, never a shell or user-controlled command fragments. Apply timeouts and output bounds. Keep secrets and sensitive file contents out of MCP results, `stderr` diagnostics, fixtures and evidence.
