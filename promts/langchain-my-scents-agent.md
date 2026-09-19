# Задание: LangChain-агент для my-scents

Реализовать приложенный план разработки [langchain_development_plan.md](langchain_development_plan.md).

План требует добавить в корень репозитория самостоятельный Python-сервис `langchain-agent/` с LangChain, ChatOllama, FastAPI, HTTPX и Pydantic. Сервис является исключительно внешним локальным клиентом публичного REST API `my-scents` и не входит в Java/Gradle-модуль, не обращается к MongoDB, Kafka, gRPC, Docker socket или внутренним классам Spring Boot.

Поддерживаются только три операции с карточками парфюмов: создание через `POST /api/v1/fragrances`, получение по UUID через `GET /api/v1/fragrances?fragranceId=<uuid>` и обновление через `PUT /api/v1/fragrances`. `DELETE` не добавлять. Мутации требуют отдельного явного подтверждения: HTTP-клиент повторяет запрос с `confirmed: true`; естественные фразы не считаются подтверждением.

Создать typed configuration, безопасный allowlist HTTP-клиент, Pydantic-модели текущих REST payload и enum, LangChain tool, prompts, нормализованный контракт `success | confirmation_required | error`, FastAPI endpoints `GET /health` и `POST /v1/agent/commands`, а также CLI. Логи tool должны содержать только operation, method, path, status и безопасный UUID без body, secrets или персональных данных. Покрыть клиент, tools, confirmation/response, API и CLI изолированными тестами без live сети или модели.

Добавить Dockerfile и Compose profile, не заявляя рабочий контейнерный запуск, пока TD-1 не исправлен. Обновить README, ARCHITECTURE.md и TECHNICAL_DEBT.md в соответствии с приложенным планом, создать примеры результатов проверок без вымышленных сквозных данных, проверить отсутствие секретов, выполнить независимое ревью, добавить только файлы задачи в Git и не создавать commit.
