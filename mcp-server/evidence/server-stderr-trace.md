# Безопасный stderr trace MCP-сервера

Этот trace фиксирует безопасные diagnostic records distribution launcher для тех же пяти tool signatures, которые фактически вызваны агентом OpenCode и сохранены в [opencode-agent-events.jsonl](opencode-agent-events.jsonl). `StdioServerSmokeTest` проверяет `initialize`, `tools/list`, все пять вызовов и отсутствие diagnostic records в MCP JSON-RPC stdout: [StdioServerSmokeTest.java:L27-L78](../src/test/java/ru/my/scents/mcp/StdioServerSmokeTest.java#L27-L78). Вложенная allowlisted проверка исключает только `StdioServerSmokeTest`, поэтому не рекурсирует и завершилась `success`; фактический отдельный вызов агентом OpenCode также завершился `success` в [opencode-agent-events.jsonl:L5](opencode-agent-events.jsonl#L5). OpenCode CLI не экспортирует дочерний stderr вместе с JSON event, поэтому связь подтверждается равными последовательностью, именами tools и безопасными параметрами; полные запросы, пути, содержимое файлов, токены, `.env` и process output не сохраняются.

```text
TOOL_CALL tool=get_local_documentation query_length=17
TOOL_RESULT tool=get_local_documentation status=success result=ok
TOOL_CALL tool=get_local_documentation query_length=5
TOOL_RESULT tool=get_local_documentation status=success result=ok
TOOL_CALL tool=search_project query_length=14
TOOL_RESULT tool=search_project status=success result=ok
TOOL_CALL tool=run_project_check check=mcp_tests
TOOL_RESULT tool=run_project_check status=success result=ok
TOOL_CALL tool=search_project query_length=10
TOOL_RESULT tool=search_project status=success result=ok
```
