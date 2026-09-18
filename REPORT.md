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
