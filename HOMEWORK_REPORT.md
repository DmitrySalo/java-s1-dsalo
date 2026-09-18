# Отчёт по AI-инструкциям

## Выбранный AI-инструмент

Для проекта специализирован **OpenCode**. Его конфигурация расширена локальными агентами в `.opencode/agents/`, тематическими правилами в `.opencode/instructions/` и иерархическими инструкциями `AGENTS.md`. Плагин `@opencode-ai/plugin` подключён в `.opencode/package.json`.

Корневой `AGENTS.md` определяет контекст, приоритет правил, границы изменений, порядок ветвления, документирование задач, формат ответов и проверочный чек-лист. `my-scents/AGENTS.md` уточняет их для Java/Spring Boot-модуля. Тематические инструкции фиксируют архитектуру, Java-стиль, тестирование, API-контракты, работу с MongoDB и безопасность.

## Сделанные доработки

В AI-инструкции добавлены:

- краткое описание проекта и мини-глоссарий;
- явная политика уточняющих вопросов и безопасных допущений;
- обязательная структура финального ответа агента;
- проверяемый чек-лист для ветки, тестов, ревью, prompt-файлов, `REPORT.md` и Git;
- три самодостаточных типовых сценария в `.opencode/instructions/workflows.md`.

## Практическая проверка

Проверка выполняется по сохранённому промпту [verify-ai-instructions.md](promts/verify-ai-instructions.md).

**Цель:** убедиться, что агент соблюдает новые инструкции при небольшом backend bugfix: delete-event использует идентификатор агрегата из `payload.id`, а не идентификатор сообщения `event_id`.

**Результат:** в `DefaultFragranceEventHandler` вызов `FragranceUseCase.delete(...)` изменён с `event.getEventId()` на `event.getPayload().getId()`. Добавлен `DefaultFragranceEventHandlerTests`, где `event_id` и `payload.id` различаются и проверяется переданный в use case ID парфюма.

**Соблюдение инструкций:**

- Работа выполнена в ветке `feature/complete-ai-homework`, созданной от обновлённой `main`.
- Перед изменением изучены Kafka handler, use case, Protobuf-контракт, существующие тесты и тематические инструкции Java, API, тестирования и безопасности.
- Protobuf-схемы, топики, публичные REST/gRPC/Kafka-контракты, зависимости и CI не изменялись.
- Тест использует Mockito и не подключается к локальной Kafka или другим внешним сервисам; `Thread.sleep` не используется.
- Изменения сохранены в prompt-файлах и последовательно отражены в `REPORT.md`.

**Проверка:**

| Команда | Результат |
| --- | --- |
| `./my-scents/gradlew.bat :my-scents:test --tests "ru.my.scents.adapter.event.consumer.handler.DefaultFragranceEventHandlerTests"` | `BUILD SUCCESSFUL`; целевой регрессионный unit-тест прошёл. |
| `./my-scents/gradlew.bat :my-scents:test` | `BUILD SUCCESSFUL`; полный набор тестов модуля прошёл. |
| `./my-scents/gradlew.bat :my-scents:build` | `BUILD SUCCESSFUL`; production-код и тесты собраны. |

Сборка сообщила о deprecated возможностях Gradle, несовместимых с Gradle 10, и предупреждениях JDK об использовании `sun.misc.Unsafe` в существующих Lombok/gRPC-зависимостях. Эти предупреждения не вызваны изменением handler-а.

**Независимое ревью:** выполнено агентом `code-reviewer`. Ревью подтвердило, что delete-сценарий использует `payload.id`, тест воспроизводит прежний дефект, а исключения не перехватываются и сохраняют работу существующего retry/DLT. Единственное замечание к отчёту о ещё не внесённых фактических результатах проверки исправлено выше; production-код после ревью не менялся, поэтому повторное ревью не требовалось.

**Архитектурные границы:** изменён только Kafka adapter, который вызывает существующий входной порт `FragranceUseCase`. Новый тест mock-ирует этот порт. Domain, MongoDB adapter, HTTP/gRPC, Protobuf-контракты и Kafka-конфигурация не затронуты.

**Оставшиеся ограничения:** тест не запускает Kafka test broker, поэтому не проверяет транспортное прохождение listener, retry и DLT. Устранение feedback loop входящих `CREATED`/`UPDATED` событий не входило в проверочный промпт и остаётся отдельной задачей технического долга.

## Трудности

Исходная реализация не содержит unit-тестов Kafka handler-а. Для проверки выбран изолированный unit-тест с mock-реализацией входного порта `FragranceUseCase`; он не требует локального Kafka broker и не меняет интеграционный контракт.

## Использованные промпты

- [research-task-1.md](promts/research-task-1.md)
- [update-opencode-instructions.md](promts/update-opencode-instructions.md)
- [update-my-scents-agents.md](promts/update-my-scents-agents.md)
- [document-task-prompts-and-report.md](promts/document-task-prompts-and-report.md)
- [add-feature-branch-rules.md](promts/add-feature-branch-rules.md)
- [complete-ai-homework.md](promts/complete-ai-homework.md)
- [verify-ai-instructions.md](promts/verify-ai-instructions.md)
