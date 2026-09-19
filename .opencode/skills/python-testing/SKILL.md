---
name: python-testing
description: Use when writing, changing, selecting, or reviewing Python unit, async, HTTP, integration, or regression tests.
---

# Python Testing

Read `.opencode/instructions/python-testing.md` and follow the existing project runner and test layout. Select the narrowest isolated test that demonstrates the change; use fakes or stubs at I/O boundaries and add a regression test for a bug fix.

Run the project's formatter, linter, type checker, and relevant test suite. Do not introduce a testing framework or use production services, real credentials, uncontrolled time, or fixed sleeps.
