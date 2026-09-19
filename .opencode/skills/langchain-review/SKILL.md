---
name: langchain-review
description: Use when reviewing LangChain agent, tool, prompt, retrieval, memory, or LLM integration changes for safety, correctness, regressions, and missing tests without modifying files.
---

# LangChain Review

Read `.opencode/instructions/langchain.md`, `.opencode/instructions/langchain-testing.md`, and `.opencode/instructions/python-style.md`. Inspect the diff and enough surrounding prompts, schemas, tools, and calling code to verify each claim. Load `langchain-api-contracts` and `langchain-security` only when applicable.

Report only actionable findings. Order them by severity and provide path, line, impact, and concrete remediation. Focus on prompt injection, unsafe tool invocation, schema validation, leakage of secrets or personal data, unbounded retries or loops, model and memory compatibility, error handling, cost controls, and deterministic testing. If there are no findings, say so and name residual testing limitations.
