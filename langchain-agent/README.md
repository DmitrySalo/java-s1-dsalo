# my-scents LangChain agent

Локальный учебный Python 3.11+ сервис. Он является внешним клиентом публичного REST API `my-scents`; не обращается к MongoDB, Kafka, gRPC, Docker или Java-пакетам. До завершения `TD-5` не предназначен для production.

## Запуск

Подготовьте локальный LLM-провайдер Ollama: установите его по [официальной инструкции](https://ollama.com/download), затем в отдельном терминале выполните:

```powershell
ollama pull llama3.2
ollama serve
```

Если в `.env` указана другая модель, подставьте её имя в `ollama pull`. Проверить доступность модели можно командой `ollama list`.

```powershell
cd langchain-agent
python -m venv .venv
.\.venv\Scripts\python -m pip install -r requirements.txt
Copy-Item .env.example .env
.\.venv\Scripts\uvicorn app.main:app --host 127.0.0.1 --port 8091
```

Нужен локальный Ollama с моделью из `OLLAMA_MODEL`; `MY_SCENTS_BASE_URL` по умолчанию указывает на `http://localhost:8087`. Значения `.env` не коммитятся. API: `GET /health` проверяет только agent-service; `POST /v1/agent/commands` принимает `{"message":"...","confirmed":false}`.

CLI использует ту же прикладную логику:

```powershell
python -m app.cli "Создай парфюм Example, рейтинг 8"
# CLI запросит отдельный интерактивный ответ «да».
```

## Контракт и границы

Каждый ответ имеет поля `status` (`success`, `confirmation_required`, `error`), `action` (`create`, `get`, `update`, `help`), `data` и `errors`. `create` и `update` сначала возвращают подготовленную операцию с одноразовым `data.prepared_operation_id`, действительным пять минут. `POST` или `PUT` выполняются только при повторном HTTP-запросе с JSON boolean `confirmed: true` и этим ID; повторного обращения к LLM нет. Прямое подтверждение, просроченный или повторно использованный ID отклоняются. Текст вроде «да» не является HTTP-подтверждением. CLI автоматически передаёт ID после интерактивного ответа «да» в том же процессе. Prepared operation хранится только в процессе и до TD-5 не привязана к caller, поэтому HTTP service разрешён исключительно на loopback-интерфейсе локальной учебной среды.

Разрешены только `POST`, `GET ?fragranceId=<uuid>` и `PUT /api/v1/fragrances`. `app/agent/factory.py` подключает единственный `fragrance_api` tool к LangChain `create_agent`; `app/my_scents/client.py` строит путь из константы. `app/agent/tools.py` пишет безопасные `TOOL_CALL`/`TOOL_RESULT` без body и программно требует разрешение policy layer на выполнение. `app/agent/service.py` выдаёт разрешение только после проверки confirmation policy. Compose profile публикуется только на `127.0.0.1`, однако старт остаётся ограничен TD-1 из-за текущего health check Java-контейнера. Подробные контрольные сценарии: [examples/verification-results.md](examples/verification-results.md).

LangChain tool и безопасные INFO-логи `TOOL_CALL`/`TOOL_RESULT` находятся в `app/agent/tools.py`; фактический REST-вызов выполняется в `app/my_scents/client.py`. Пример выбора действия: запрос `Получи парфюм с UUID <uuid>` должен привести к операции `get` и вызову `GET /api/v1/fragrances?fragranceId=<uuid>`.

## Использованные промпты

- [Системный prompt](prompts/system.md) задаёт роль API-оператора, allowlist операций, защиту от prompt injection и JSON Schema намерения.
- [Пользовательские шаблоны](prompts/user-templates.md) содержат примеры получения, создания, обновления, подтверждения и безопасной ошибки.
