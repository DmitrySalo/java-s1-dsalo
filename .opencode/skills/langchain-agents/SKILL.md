---
name: langchain-agents
description: Use when implementing or refactoring LangChain agents, tools, chains, structured output, retrieval, memory, or LLM integrations.
---

# LangChain Agents

Read `.opencode/instructions/langchain.md` and `.opencode/instructions/python-style.md`. Inspect existing prompts, message schemas, tools, models, retrievers, and tests before editing. Keep deterministic business logic outside the LLM and make tool boundaries narrow, validated, and observable.

Load `langchain-security`, `langchain-api-contracts`, and `langchain-testing` only when their topic applies. Use fake/stub LLMs and tools for tests; do not require real API keys, network access, production models, or vector stores. Bound retries, timeouts, context size, and tool iterations explicitly.
