---
name: mcp-testing
description: Use when adding, changing, selecting, or reviewing tests and IDE acceptance evidence for a local MCP server.
---

# MCP Testing

Read `.opencode/instructions/mcp-testing.md`. Test tools through isolated fixtures and a deterministic temporary project root. Cover result contracts, validation, error paths, path traversal, denylisted files, process allowlists, limits and safe `stderr` logs.

Run the narrowest module test first, then the module build. Record exact executed commands and actual results. Treat IDE traces and screenshots as acceptance evidence, not a substitute for automated tests.
