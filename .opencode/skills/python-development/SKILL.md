---
name: python-development
description: Use when implementing or refactoring senior-level Python services, CLIs, integrations, asynchronous code, or tests.
---

# Python Development

Read `.opencode/instructions/python-style.md` and follow the existing project structure and tooling. Preserve explicit I/O boundaries, type safety, resource lifecycle, and safe exception handling. Inspect analogous production code, dependency configuration, and tests before editing.

Load `python-api-contracts`, `python-security`, and `python-testing` only when their topic applies. Use the project's existing formatter, linter, type checker, and test runner; run the narrowest relevant verification before the full applicable suite. Do not use real credentials or external production services in tests.
