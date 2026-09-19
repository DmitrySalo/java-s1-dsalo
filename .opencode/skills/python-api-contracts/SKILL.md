---
name: python-api-contracts
description: Use when creating or changing Python HTTP APIs, request or response schemas, errors, webhooks, authentication requirements, pagination, or OpenAPI contracts.
---

# Python API Contracts

Read `.opencode/instructions/python-api-contracts.md` and `.opencode/instructions/python-security.md`. Preserve existing URI, JSON, error, authentication, and versioning conventions. Treat removed, renamed, retyped, or newly required fields and changed status semantics as breaking until compatibility is demonstrated.

Validate input at the API boundary, enforce authorization server-side, and keep persistence models and internal exceptions out of responses. Add focused HTTP or contract tests for changed behavior, validation, authorization, and error handling.
