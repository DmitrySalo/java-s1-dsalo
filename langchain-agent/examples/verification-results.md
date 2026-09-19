# Результаты проверок

## Автоматизированная проверка, 2026-09-19

Команда:

```powershell
.\.venv\Scripts\python.exe -m pytest tests/test_my_scents_client.py tests/test_tools.py tests/test_agent_response.py tests/test_agent_api.py tests/test_cli.py
```

Результат: `30 passed, 1 warning` (`anyio.abc.BlockingPortal` deprecated в зависимости Starlette). Тесты с `httpx.MockTransport` подтвердили реальные для клиента HTTP method/path/query/body: `POST /api/v1/fragrances`, `GET /api/v1/fragrances?fragranceId=...`, `PUT /api/v1/fragrances`; проверены stderr-вывод `TOOL_CALL`/`TOOL_RESULT` без body и URI, передача естественно-языкового ввода планировщику, маршрутизация `get`, запрет `delete`, отсутствие вызова мутации без `confirmed=true`, одноразовость prepared-operation ID, строгий JSON boolean, ошибка Ollama и справка без upstream-вызова.

## Сквозные сценарии

19 сентября 2026 года выполнены пять разных запросов к локальным Ollama и `my-scents`: create с подтверждением, get по созданному UUID, update с подтверждением, create без подтверждения и help без tool-вызова. Подтверждённые операции выполнили `POST`, `GET` и `PUT` с `200 OK`; неподтверждённое создание и help не вызвали tool. Точные входы, нормализованные ответы, подтверждения, UUID тестовой сущности и фактические безопасные строки `TOOL_CALL`/`TOOL_RESULT` приведены в разделе [«Воспроизводимые артефакты сквозной проверки»](../../LANGCHAIN_HOMEWORK_REPORT.md#воспроизводимые-артефакты-сквозной-проверки).
