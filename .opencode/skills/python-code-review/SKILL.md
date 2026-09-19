---
name: python-code-review
description: Use when reviewing Python code changes for correctness, regressions, security, typing, resource handling, and missing tests without modifying files.
---

# Python Code Review

Read `.opencode/instructions/python-style.md`, `.opencode/instructions/python-testing.md`, and inspect the diff with enough surrounding code to verify each claim. Load `python-api-contracts` and `python-security` only when applicable.

Report only actionable findings. Order them by severity and provide path, line, impact, and concrete remediation. Focus on unsafe input handling, type contract violations, exception paths, resource cleanup, sync/async boundaries, concurrency, public compatibility, and deterministic tests. If there are no findings, say so and name residual testing limitations.
