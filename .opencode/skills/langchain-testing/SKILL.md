---
name: langchain-testing
description: Use when writing, changing, selecting, or reviewing LangChain agent, tool, prompt, structured output, retrieval, memory, or LLM integration tests.
---

# LangChain Testing

Read `.opencode/instructions/langchain-testing.md`. Use fake/stub models and tools for deterministic tests; in LangChain Python, `GenericFakeChatModel` can provide scripted messages and tool calls. Test schemas, policy, routing, error handling, retries, and state transitions rather than prose wording.

Keep live-provider checks separate with an explicit integration marker and never require keys, network, or production stores for the regular suite.
