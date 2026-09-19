# Журнал работ

## 2026-09-18 -- Добавление агентов, инструкций и документации

**Задание:** [research-task-1.md](promts/research-task-1.md).

Изучена фактическая реализация модульного монолита «Ароматека»: Gradle-конфигурация, Java-код, Protobuf-контракты, конфигурация Spring Boot, Docker Compose, скрипты локальной инфраструктуры и тесты. Добавлены `ARCHITECTURE.md`, корневой `AGENTS.md`, набор агентов и skills OpenCode, тематические инструкции `.opencode/instructions` и `promts/research-task-1.md`. Корневой `README.md` заменён актуальным руководством разработчика.

## 2026-09-18 -- Актуализация инструкций для агентов и разработчиков

**Задание:** [update-opencode-instructions.md](promts/update-opencode-instructions.md).

Актуализированы все файлы `.opencode/instructions` по фактической архитектуре «Ароматеки». Инструкции теперь фиксируют Java 25/Spring Boot/MongoDB стек, границы `boundary`/`domain`/`adapter`/`infra`, текущие REST-, gRPC- и Kafka-контракты, правила расширения Protobuf и обработки событий, Testcontainers-проверки, эксплуатационные ограничения и отсутствие frontend-модуля. Удалены неприменимые требования JPA/Flyway/PostgreSQL и React/Vitest; добавлены проектные пошаговые сценарии и примеры реализации.

## 2026-09-18 -- Актуализация инструкций модуля my-scents

**Задание:** [update-my-scents-agents.md](promts/update-my-scents-agents.md).

Обновлён `my-scents/AGENTS.md` по корневым правилам, `ARCHITECTURE.md` и `.opencode/instructions`. В модульной инструкции зафиксированы Java 25/Spring Boot 3.5.6, MongoDB через `MongoTemplate`, границы Clean/Hexagonal Architecture, REST/gRPC/Kafka контракты, Protobuf-совместимость, retry/DLT, безопасность, наблюдаемость, команды Gradle и выбор тестов. Удалены устаревшие ссылки на Java 21, Spring Boot 4.1.0, JPA, Flyway, PostgreSQL и несуществующую задачу `verify`.

## 2026-09-18 -- Обязательное документирование заданий и журнала работ

**Задание:** [document-task-prompts-and-report.md](promts/document-task-prompts-and-report.md).

Созданы недостающие файлы заданий для ранее добавленных записей журнала. Корневые, модульные и агентские инструкции дополнены обязательным процессом: prompt-файл создаётся для каждого задания, а новая запись добавляется только в конец `REPORT.md` и обязательно ссылается на этот файл.

## 2026-09-18 -- Правила работы в feature-ветках

**Задание:** [add-feature-branch-rules.md](promts/add-feature-branch-rules.md).

Корневые и модульные инструкции, а также правила backend-, frontend- и архитектурного агентов дополнены обязательным процессом: обновить `main` из удалённого репозитория, создать от неё лаконичную ветку `feature/` в `kebab-case` и выполнять изменения только в этой ветке. Установлена текущая ветка `feature/branching-rules`, созданная от актуальной `main`.

## 2026-09-18 -- Декомпозиция технического долга

**Задание:** [document-technical-debt.md](promts/document-technical-debt.md).

Создан `TECHNICAL_DEBT.md` с подробным бэклогом по каждому ограничению из `ARCHITECTURE.md`: согласование контейнерных портов, transactional outbox, обработка Kafka-событий, внешние контракты, безопасность и автоматизированное тестирование. Для задач определены риски, последовательность работ, критерии приёмки и порядок выполнения.

## 2026-09-18 -- Доработка AI-инструкций для OpenCode

**Задание:** [complete-ai-homework.md](promts/complete-ai-homework.md).

Корневые AI-инструкции дополнены описанием контекста, глоссарием, правилами уточнений, единым форматом ответа и проверочным чек-листом. Создан документ типовых сценариев OpenCode и отдельный отчёт для сдачи задания.

## 2026-09-18 -- Практическая проверка AI-инструкций

**Задание:** [verify-ai-instructions.md](promts/verify-ai-instructions.md).

Выполнен изолированный bugfix обработки Kafka delete-event: ID парфюма теперь берётся из `payload.id`. Добавлен регрессионный unit-тест с различающимися `event_id` и `payload.id`; результаты проверок и независимого ревью зафиксированы в `HOMEWORK_REPORT.md`.

## 2026-09-19 -- Добавление Python и LangChain агентов

**Задание:** [add-python-langchain-agents.md](promts/add-python-langchain-agents.md).

Добавлены senior-конфигурации разработчиков и ревьюеров Python и LangChain, тематические инструкции и skills. В `scripts/AGENTS.md` закреплены правила безопасной разработки Python-скриптов и LangChain-агентов; корневой `AGENTS.md` дополнен ссылками на новые роли и независимое ревью.

## 2026-09-19 -- Специализированные Python и LangChain инструкции

**Задание:** [python-langchain-specialized-instructions.md](promts/python-langchain-specialized-instructions.md).

Добавлены отдельные инструкции и skills для API-контрактов, безопасности и тестирования Python и LangChain. Конфигурации агентов, базовые skills и `scripts/AGENTS.md` теперь ссылаются на специализированные правила вместо Java-ориентированных инструкций.

## 2026-09-19 -- Учебный LangChain-сервис для публичного API парфюмов

**Задание:** [langchain-my-scents-agent.md](promts/langchain-my-scents-agent.md).

Добавлен отдельный локальный Python-сервис `langchain-agent` с ограниченным REST-клиентом, Pydantic-моделями текущего контракта парфюмов, LangChain/ChatOllama intent parser, allowlisted tool, программным подтверждением мутаций, FastAPI и CLI. Добавлены изолированные тесты, Compose profile, документация архитектурных границ и технического долга. Реальные сквозные сценарии с Ollama и my-scents не выполнялись и не задокументированы как успешные.

## 2026-09-19 -- Проверка соответствия LangChain-плану

**Задание:** [check-langchain-plan-compliance.md](promts/check-langchain-plan-compliance.md).

Проверено соответствие `langchain-agent` плану разработки. `fragrance_api` подключён к LangChain-агенту и защищён программным policy layer; подтверждение мутаций использует одноразовый ID с TTL и не вызывает LLM повторно. Исправлена API-регрессия теста и добавлены проверки replay-защиты и безопасных логов. Реальные сквозные сценарии с Ollama и my-scents остаются невыполненными.

## 2026-09-19 -- Сквозная проверка LangChain-агента

**Задание:** [langchain-homework-verification.md](promts/langchain-homework-verification.md).

Выполнены пять реальных LLM-запросов через локальные Ollama и LangChain-агент, а также три upstream-операции `POST`/`GET`/`PUT` против локального `my-scents`. Добавлен `LANGCHAIN_HOMEWORK_REPORT.md` с настройкой модели, границами API, командами запуска и результатами. Для локального Ollama явно подключена JSON Schema-стратегия структурированного вывода; добавлен регрессионный тест её выбора.

## 2026-09-19 -- Устранение недочётов проверки LangChain homework

**Задание:** [fix-langchain-homework-compliance.md](promts/fix-langchain-homework-compliance.md).

Синхронизированы доказательства сквозной проверки: зафиксированы пять разных запросов, включая неподтверждённое создание без tool-вызова, и три подтверждённых HTTP-операции с `200 OK`. Обновлён фактический результат изолированного набора тестов (`27 passed, 1 warning`); в `LANGCHAIN_HOMEWORK_REPORT.md` добавлены точные ссылки на реализацию LangChain tool, HTTP-вызова, безопасных логов, контракта ответа и промптов. Устранено ошибочное утверждение архитектурного документа о логировании HTTP-статуса tool.

## 2026-09-19 -- Финальная проверка и устранение недочётов LangChain homework

**Задание:** [final-langchain-homework-compliance.md](promts/final-langchain-homework-compliance.md).

Для LangChain tool включён самостоятельный безопасный INFO-вывод `TOOL_CALL`/`TOOL_RESULT` в stderr при штатном запуске, без body, URL и учётных данных. Документация дополнена воспроизводимой подготовкой Ollama и явным списком использованных промптов. Добавлены изолированные проверки console-лога, передачи естественно-языкового ввода планировщику, маршрутизации `get`, запрета недопустимого намерения и подключения system prompt.
