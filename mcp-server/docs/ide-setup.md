# Настройка OpenCode

Поддерживается только OpenCode. Воспроизводимый сценарий:

1. Из `mcp-server` выполните `./gradlew.bat installDist`.
2. Скопируйте блок `mcp.my-scents-local` из `config/opencode.mcp.json.example` в корневой `opencode.json`.
3. Оставьте `cwd` равным `mcp-server`, command равным `build/install/mcp-server/bin/mcp-server.bat`, а `MCP_PROJECT_ROOT` равным `..`.
4. Перезапустите OpenCode и выполните `opencode mcp list`; ожидаемый статус -- `my-scents-local connected`.
5. Отправьте запрос «Найди локальные правила безопасного запуска проверок проекта.»; ожидаемый tool -- `get_local_documentation`.

Процесс запускается напрямую distribution launcher, а не через Gradle. В `stdio`-режиме `stdout` принадлежит только MCP JSON-RPC; диагностические `TOOL_CALL` и `TOOL_RESULT` пишутся сервером только в `stderr`.
