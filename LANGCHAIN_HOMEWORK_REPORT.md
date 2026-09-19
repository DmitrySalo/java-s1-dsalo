# Отчёт по домашнему заданию LangChain

## Область проверки

19 сентября 2026 года локальный `langchain-agent` прошёл сквозную проверку с запущенными локальными Ollama и `my-scents`. Проверка выполнялась через прикладной сервис агента; в отчёте приведены только безопасные метаданные операций и идентификатор тестового парфюма, созданного при проверке.

## LLM

Агент использует локальную Ollama через LangChain `ChatOllama`:

- модель: `llama3.2:latest` (в конфигурации задана `llama3.2`, Ollama разрешает её в локальный тег `latest`);
- URL провайдера: `http://localhost:11434`;
- температура: `0`;
- лимит вывода: `512` токенов;
- тайм-аут запроса: `30` секунд.

Агент задаёт `ProviderStrategy(Intent)`, чтобы Ollama ограничивала результат планирования JSON Schema типа `Intent`. Это предотвращает ненадёжный автоматический fallback в tool-calling, наблюдавшийся с локальной моделью `llama3.2`. Приложение валидирует этот результат до любого вызова API.

Настройте локальную модель в `langchain-agent/.env`, скопировав `.env.example`:

```dotenv
MY_SCENTS_BASE_URL=http://localhost:8087
OLLAMA_BASE_URL=http://localhost:11434
OLLAMA_MODEL=llama3.2
```

Модель должна быть предварительно установлена в локальную Ollama. Следуйте командам `ollama pull llama3.2` и `ollama serve` из `langchain-agent/README.md`; во время проверки `GET /api/tags` вернул `llama3.2:latest`, а фактический запрос генерации вернул `READY`.

## API и операции

В качестве upstream выбран публичный REST API `my-scents`: `http://localhost:8087/api/v1/fragrances`. Агент имеет один tool из allowlist и поддерживает только следующие операции:

| Действие агента | HTTP-операция | Upstream endpoint | Подтверждение |
| --- | --- | --- | --- |
| `create` | `POST` | `/api/v1/fragrances` | Требуется |
| `get` | `GET` | `/api/v1/fragrances?fragranceId=<uuid>` | Не требуется |
| `update` | `PUT` | `/api/v1/fragrances` | Требуется |
| `help` | Нет | Нет | Не применимо |

`create` и `update` сначала возвращают одноразовый `prepared_operation_id`, действующий пять минут. Они выполняются только вторым запросом с JSON boolean `confirmed: true` и этим ID. Подтверждение не делает ещё один запрос к LLM. Операции удаления, поиска, произвольного HTTP, shell, файловой системы, базы данных, Kafka, gRPC и Docker недоступны.

## Запуск

Установите Ollama по [официальной инструкции](https://ollama.com/download), затем выполните `ollama pull llama3.2` и `ollama serve` в отдельном терминале. После этого запустите `my-scents` на порту `8087`. Из корня репозитория выполните:

```powershell
cd langchain-agent
python -m venv .venv
.\.venv\Scripts\python -m pip install -r requirements.txt
Copy-Item .env.example .env
.\.venv\Scripts\uvicorn app.main:app --host 127.0.0.1 --port 8091
```

Проверьте сервис агента:

```powershell
Invoke-RestMethod http://127.0.0.1:8091/health
```

Используйте endpoint команд:

```powershell
Invoke-RestMethod -Method Post http://127.0.0.1:8091/v1/agent/commands -ContentType application/json -Body '{"message":"Show supported fragrance operations without calling the API.","confirmed":false}'
```

## Фактическая проверка

Следующие пять фактических запросов достигли локальной LLM через агента после включения JSON Schema-стратегии. Результаты являются ответами, валидированными прикладным уровнем.

| № | Назначение LLM-запроса | Результат |
| --- | --- | --- |
| 1 | Создание полной карточки парфюма `Compliance Check 20260919` с рейтингом `7`. | `confirmation_required`, действие `create`; имя и рейтинг корректно распознаны, выдан ID подготовленной операции. |
| 2 | Получение парфюма `969fce5b-0f61-40d5-8021-f9cd9e889293`. | `success`, действие `get`; возвращён `Compliance Check 20260919` с рейтингом `7`. |
| 3 | Обновление этой карточки с именем `Compliance Check Updated 20260919` и рейтингом `8`. | `confirmation_required`, действие `update`; выдан ID подготовленной операции. |
| 4 | Вывод поддерживаемых операций без обращения к API. | `success`, действие `help`; возвращены `create`, `get`, `update`. |
| 5 | Создание `Unconfirmed Compliance 20260919` без подтверждения. | `confirmation_required`, действие `create`; выдан ID подготовленной операции, HTTP `POST` не выполнялся. |

Через tool `fragrance_api` из allowlist выполнены три фактические upstream-операции:

| № | Upstream-операция | Результат |
| --- | --- | --- |
| 1 | Подтверждённый `POST /api/v1/fragrances` для подготовленного создания. | `200 OK`; создан UUID `969fce5b-0f61-40d5-8021-f9cd9e889293`, имя `Compliance Check 20260919`, рейтинг `7`. |
| 2 | `GET /api/v1/fragrances?fragranceId=969fce5b-0f61-40d5-8021-f9cd9e889293`. | `200 OK`; возвращена созданная карточка с рейтингом `7`. |
| 3 | Подтверждённый `PUT /api/v1/fragrances` для подготовленного обновления. | `200 OK`; возвращён тот же UUID с именем `Compliance Check Updated 20260919` и рейтингом `8`. |

Две операции записи не отправлялись до отдельных запросов подтверждения. Запрос `help` и неподтверждённое создание не вызвали upstream.

## Воспроизводимые артефакты сквозной проверки

Ниже приведены точные естественно-языковые входы и нормализованные результаты пяти запросов. Для `create` и `update` в таблице указаны только статусы подготовительного ответа: одноразовые `prepared_operation_id` не сохраняются, так как действуют пять минут и не нужны для воспроизведения результата.

| Сценарий | Точный запрос | Нормализованный результат |
| --- | --- | --- |
| create | `Создай парфюм Compliance Check 20260919, рейтинг 7, описание citrus woody fragrance, EAU_DE_PARFUM, WOODY, UNISEX, FALL, MODERATE, MODERATE, AVAILABLE` | `{"status":"confirmation_required","action":"create"}`; после отдельного `confirmed=true` -- `{"status":"success","action":"create","data":{"id":"969fce5b-0f61-40d5-8021-f9cd9e889293","name":"Compliance Check 20260919","rating":7}}` |
| get | `Получи парфюм с UUID 969fce5b-0f61-40d5-8021-f9cd9e889293` | `{"status":"success","action":"get","data":{"id":"969fce5b-0f61-40d5-8021-f9cd9e889293","name":"Compliance Check 20260919","rating":7}}` |
| update | `Обнови парфюм с UUID 969fce5b-0f61-40d5-8021-f9cd9e889293: имя Compliance Check Updated 20260919, рейтинг 8, описание updated citrus woody fragrance, EAU_DE_PARFUM, WOODY, UNISEX, FALL, STRONG, MODERATE, AVAILABLE` | `{"status":"confirmation_required","action":"update"}`; после отдельного `confirmed=true` -- `{"status":"success","action":"update","data":{"id":"969fce5b-0f61-40d5-8021-f9cd9e889293","name":"Compliance Check Updated 20260919","rating":8}}` |
| create без подтверждения | `Создай парфюм Unconfirmed Compliance 20260919, рейтинг 6, описание should not be sent, EAU_DE_PARFUM, WOODY, UNISEX, FALL, MODERATE, MODERATE, AVAILABLE` | `{"status":"confirmation_required","action":"create"}`; подтверждение не отправлялось, HTTP-вызова не было. |
| help | `Покажи поддерживаемые операции без обращения к API.` | `{"status":"success","action":"help","data":{"operations":["create","get","update"]}}` |

Фактические безопасные строки лога для трёх вызовов tool:

```text
TOOL_CALL operation=create method=POST path=/api/v1/fragrances resource_id=
TOOL_RESULT operation=create method=POST path=/api/v1/fragrances resource_id=969fce5b-0f61-40d5-8021-f9cd9e889293
TOOL_CALL operation=get method=GET path=/api/v1/fragrances resource_id=969fce5b-0f61-40d5-8021-f9cd9e889293
TOOL_RESULT operation=get method=GET path=/api/v1/fragrances resource_id=969fce5b-0f61-40d5-8021-f9cd9e889293
TOOL_CALL operation=update method=PUT path=/api/v1/fragrances resource_id=969fce5b-0f61-40d5-8021-f9cd9e889293
TOOL_RESULT operation=update method=PUT path=/api/v1/fragrances resource_id=969fce5b-0f61-40d5-8021-f9cd9e889293
```

## Реализация tool и наблюдаемость

Единственный LangChain tool `fragrance_api` объявлен в `langchain-agent/app/agent/tools.py:60-86`. Его ветви вызывают ограниченный HTTP-клиент в строках `71-82`; реальный вызов `httpx.Client.request(...)` расположен в `langchain-agent/app/my_scents/client.py:34-40`. `configure_tool_logging()` в `langchain-agent/app/agent/tools.py:21-33` подключает безопасный INFO-handler к stderr, а `TOOL_CALL` и `TOOL_RESULT` выводятся в строках `70,83`; они содержат операцию, HTTP-метод, путь и UUID ресурса, но не содержат body, URL провайдера или учётные данные.

Пример маршрутизации: запрос `Получи парфюм с UUID <uuid>` интерпретируется как `get` и вызывает `GET /api/v1/fragrances?fragranceId=<uuid>`.

## Контракт ответа и промпты

Требования к фиксированному ответу описаны в `langchain-agent/README.md:33-39` и реализованы в `langchain-agent/app/agent/response.py:9-27`: `status`, `action`, `data`, `errors`. Системный prompt находится в `langchain-agent/prompts/system.md:1-8`, пользовательские шаблоны и примеры -- в `langchain-agent/prompts/user-templates.md:1-8`.
