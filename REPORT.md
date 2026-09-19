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

## 2026-09-19 -- Каркас разработки MCP-сервера

**Задание:** [mcp-server-foundation.md](promts/mcp-server-foundation.md).

Создана ветка `feature/mcp-server-foundation` от указанной `feature/python-langchain-agents`. Добавлены специализированные MCP-роли, инструкции и skills для Java MCP-сервера с `stdio`, файловым sandbox, allowlist процессов, тестированием и независимым ревью. Создан пустой модуль `mcp-server` с локальными правилами разработки; SDK, сборка и реализация tools пока не добавлялись.

## 2026-09-19 -- Локальный MCP-сервер с безопасными tools

**Задание:** [mcp-server-development-plan.md](promts/mcp-server-development-plan.md).

Создан автономный Java 25 MCP stdio-модуль с официальным MCP Java SDK, tool для локальной документации, ограниченным поиском по trusted project root и статическим allowlist проверок. Добавлены filesystem sandbox, bounded process output, безопасные stderr-логи, Gradle Wrapper, тесты, документация и шаблоны конфигураций OpenCode, IntelliJ IDEA и VS Code. Независимое review подтвердило и после исправления перепроверены критичные границы stdout, файлового доступа и process output. Интерактивная приёмка в IDE и screenshots не выполнены: в текущей среде недоступен интерактивный MCP-клиент; evidence честно помечено pending.

## 2026-09-19 -- Настройка OpenCode для локального MCP-сервера

**Задание:** [configure-opencode-mcp.md](promts/configure-opencode-mcp.md).

Собран application distribution MCP-сервера через `installDist`. В корневой `opencode.json` добавлен локальный `stdio`-сервер `my-scents-local`, запускающий distribution launcher с trusted project root `.`. Шаблон OpenCode-конфигурации синхронизирован: недопустимый placeholder `${workspaceFolder}` заменён на относительное значение `.`.

## 2026-09-19 -- Приёмка MCP-инструментов через OpenCode

**Задание:** [mcp-ide-acceptance.md](promts/mcp-ide-acceptance.md).

OpenCode 1.18.3 подтвердил подключение `my-scents-local`. Реальный MCP `stdio`-сеанс подтвердил initialize и выполнил пять tool calls: local documentation, project search, MCP tests, backend tests и документацию stdio. Четыре сценария завершились успешно; backend check вернул `exit_code: 1`, поскольку Windows не запускает `.bat` напрямую через `ProcessBuilder`, а shell запрещён policy. Сохранён безопасный текстовый trace без путей, токенов, `.env` и персональных данных. Скриншоты не созданы: интерактивное окно IDE в среде недоступно, synthetic evidence сознательно не добавлялось.

## 2026-09-19 -- Перевод отчёта MCP-сервера на русский язык

**Задание:** [translate-mcp-homework-report.md](promts/translate-mcp-homework-report.md).

Содержимое `mcp-server/MCP_SERVER_HOMEWORK_REPORT.md` переведено на русский язык без изменения ссылок на код, фактических результатов приёмки и описанного ограничения запуска `backend_tests` на Windows.

## 2026-09-19 -- Перевод документации и evidence MCP-сервера на русский язык

**Задание:** [translate-mcp-docs-and-evidence.md](promts/translate-mcp-docs-and-evidence.md).

На русский язык переведены все Markdown-файлы в `mcp-server/docs` и текстовый trace в `mcp-server/evidence`. Имена tools, переменных окружения, файлов, команды и фактические безопасные stderr-события сохранены без изменения.

## 2026-09-19 -- Устранение рисков и недоработок MCP-сервера

**Задание:** [fix-mcp-server-compliance.md](promts/fix-mcp-server-compliance.md).

Исправлен контракт process tool: неуспешный exit code возвращает структурированную ошибку, allowlist сокращён до воспроизводимой фиксированной проверки MCP-модуля, а вывод дочернего процесса не передаётся MCP-клиенту. При timeout завершается процесс и его descendants. Добавлены изолированные проверки contract/logging, process error/timeout и ограничений filesystem sandbox; документация, OpenCode-steps, JSON-RPC evidence и `MCP_SERVER_HOMEWORK_REPORT.md` дополнены воспроизводимыми подтверждениями с диапазонами строк. Независимое MCP review выявило и после исправления подтвердило устранение рисков process output и timeout; для запуска distribution требуется JDK 25 в `JAVA_HOME`.

## 2026-09-19 -- Полное закрытие критериев MCP-сервера

**Задание:** [complete-mcp-server-acceptance.md](promts/complete-mcp-server-acceptance.md).

Устранены выявленные риски файлового чтения и process output: разрешённый файл повторно проверяется и открывается с `NOFOLLOW_LINKS`, а output allowlisted Gradle-проверки полностью дренируется после достижения лимита без передачи клиенту и ожидание reader ограничено. Добавлены негативные проверки absolute/platform-invalid path, symbolic links и лимита обхода файлов, а также автоматический stdio smoke-test distribution launcher для `initialize`, `tools/list`, input schema, tool call, JSON-RPC-only stdout и безопасных stderr-логов. Повторный OpenCode MCP status с JDK 25 подтвердил `my-scents-local connected`; README, evidence и отчёт дополнены проверяемыми ссылками. Повторный независимый review-agent временно недоступен из-за исчерпанного upstream request budget; замечания первого независимого review устранены и проверены регрессионными тестами.

## 2026-09-19 -- Исправление замечаний проверки MCP-сервера

**Задание:** [fix-mcp-review-findings.md](promts/fix-mcp-review-findings.md).

Устранено падение search-tool на binary fixtures, запрещён поиск файлов с чувствительными именами, добавлены лимит обхода documentation и безопасная обработка runtime I/O ошибок. С JDK 25 успешно выполнены tests, build, distribution smoke-test и подключение OpenCode; сохранены фактические session/call identifiers пяти вызовов MCP tools агентом OpenCode. Обновлены README, evidence и отчёт с точными ссылками на код и контракт результата.

## 2026-09-20 -- Полное закрытие критериев MCP-сервера

**Задание:** [complete-mcp-server-criteria.md](promts/complete-mcp-server-criteria.md).

Ограничен анализ каждой строки поиска первыми 500 символами и добавлен регрессионный тест. Удалены конфигурации IntelliJ IDEA и VS Code: единственным поддерживаемым MCP-клиентом оставлен OpenCode. Сохранены новые фактические события пяти вызовов tools агентом OpenCode, а smoke-test повторяет их безопасные signatures, подтверждая server stderr и JSON-RPC-only stdout. Обновлены README, evidence и отчёт приёмки с ссылками на код и диапазоны строк.

## 2026-09-20 -- Устранение замечаний приёмки MCP-сервера

**Задание:** [fix-mcp-acceptance-review.md](promts/fix-mcp-acceptance-review.md).

Исправлен путь к примеру конфигурации OpenCode в воспроизводимой инструкции. Для MCP SDK 0.18.4 добавлена явная boundary-проверка входных параметров tools: обязательные и лишние поля, отсутствующий или пустой `arguments`, JSON-типы, длины, диапазоны и enum отклоняются структурированной ошибкой до запуска реализации; `stdio` smoke-test покрывает двенадцать таких отрицательных запросов и полный error contract. Логи не преобразуют произвольный JSON input в строку; проверен безопасный отказ object вместо string с последующим валидным вызовом. Уточнена граница доверия файлового sandbox: configured `MCP_PROJECT_ROOT` должен быть неизменяемым для параллельных процессов. Актуализированы архитектурная дата, версия MCP SDK и ссылки отчёта приёмки.

## 2026-09-20 -- Устранение рисков файлового sandbox MCP-сервера

**Задание:** [fix-mcp-sandbox-risks.md](promts/fix-mcp-sandbox-risks.md).

Поисковые MCP tools используют ограниченные immutable snapshots, поэтому не читают файлы во время tool-вызова; добавлены лимиты snapshot, безопасные structured errors при недоступном initial scan и denylist вариантов имён секретных файлов. Дополнены regression-, stdio smoke-тесты, документация, архитектура и отчёт приёмки. Initial scan по-прежнему требует неизменяемого trusted root на Windows из-за отсутствия portable descriptor-relative Java NIO API.

## 2026-09-20 -- Устранение неограниченного вывода MCP-проверки

**Задание:** [fix-mcp-process-output-limit.md](promts/fix-mcp-process-output-limit.md).

Для `run_project_check` добавлен общий лимит 8 KiB объединённого вывода дочернего процесса. При его превышении сервер завершает process tree и возвращает безопасную структурированную ошибку `OUTPUT_LIMIT_EXCEEDED`, не передавая child-process output MCP-клиенту. Добавлен регрессионный тест остановки процесса, обновлены README и отчёт приёмки; будут повторно выполнены модульные, stdio, OpenCode и security-проверки.

## 2026-09-20 -- Полная проверка критериев MCP-сервера

**Задание:** [verify-mcp-server-criteria.md](promts/verify-mcp-server-criteria.md).

Повторно успешно выполнены узкий process-regression test, stdio smoke-test distribution launcher, полный test, build и installDist. OpenCode подтвердил подключение `my-scents-local` и фактически вызвал `run_project_check(mcp_tests)` с успешным результатом. Проверены trace пяти запросов, code links, server stderr logging и отсутствие отслеживаемых секретов. После независимого review возвращён кроссплатформенный fallback завершения descendants до и после остановки parent process; непроверяемая native Windows Job Object реализация сохранена как ранее staged работа и не используется.

## 2026-09-20 -- Windows Job containment для проверки проекта

**Задание:** [windows-job-process-containment.md](promts/windows-job-process-containment.md).

Для `run_project_check` на Windows процесс создаётся через `CreateProcessW` в suspended state, назначается Job Object с `KILL_ON_JOB_CLOSE` и только затем возобновляется. Containment закрывается при штатном завершении, timeout, превышении output limit, interruption и ошибке reader. Добавлен реальный Windows integration test: JVM helper создаёт долгоживущий child process, после закрытия Job проверяется завершение обоих PID. Независимое MCP review выявило риск fallback Process API, stdin inheritance, quoting аргументов и cleanup теста; он устранён до повторного запуска targeted проверок. Полный `test` не завершился за увеличенный лимит из-за зависания существующего stdio smoke-test при вложенной Gradle-проверке; production build без тестов успешно выполнен.

## 2026-09-20 -- Устранение зависания stdio smoke-test

**Задание:** [fix-stdio-smoke-test-hang.md](promts/fix-stdio-smoke-test-hang.md).

`StdioServerSmokeTest` больше не вызывает позитивный `run_project_check(mcp_tests)` и не запускает вложенный Gradle process. Он сохраняет protocol-boundary проверку недопустимого значения check; успешная маршрутизация валидного check через handler проверяется с fake process boundary, а контракт process tool и Windows Job containment -- отдельно изолированными и integration тестами. Это устраняет рекурсивную цепочку Gradle из MCP transport smoke-test.
