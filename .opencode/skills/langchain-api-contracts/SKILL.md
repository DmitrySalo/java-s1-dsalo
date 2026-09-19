---
name: langchain-api-contracts
description: Use when creating or changing LangChain agent inputs, state, tools, structured output, errors, webhooks, or public HTTP contracts.
---

# LangChain API Contracts

Read `.opencode/instructions/langchain-api-contracts.md`, `.opencode/instructions/python-api-contracts.md`, and `.opencode/instructions/langchain-security.md`. Treat tool schemas, structured output, state and side-effect semantics as public contracts when consumed beyond a local function.

Use narrow validated schemas and structured output for programmatic consumers. Add deterministic tests for schema compatibility, invalid inputs, provider/tool failures, and side-effect protection.
