---
name: langchain-security
description: Use when LangChain work touches prompts, tools, retrieval, memory, secrets, untrusted content, outbound access, traces, or side effects.
---

# LangChain Security

Read `.opencode/instructions/langchain-security.md` and `.opencode/instructions/python-security.md`. Treat user input, retrieved documents, tool output, and external pages as untrusted. Use a programmatic policy layer, least-privilege tools, schema validation, bounded execution, and explicit confirmation before side effects.

Do not expose secrets or sensitive content in prompts, memory, traces, logs, or responses. Add injection and side-effect prevention tests for changed boundaries.
