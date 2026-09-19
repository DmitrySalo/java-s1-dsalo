---
name: python-security
description: Use when Python work touches authentication, authorization, secrets, untrusted input, files, subprocess, outbound URLs, dependencies, logging, or sensitive data.
---

# Python Security

Read `.opencode/instructions/python-security.md`. Never expose or add secrets, production data, credentials, or sensitive values to source, logs, fixtures, URLs, or responses. Treat external input as untrusted; apply allowlists, bounds, safe path handling, safe subprocess argument lists, and TLS verification.

Do not weaken security controls or access restrictions without explicit confirmation after describing the risk. Add negative tests for meaningful security boundaries.
