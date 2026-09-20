# Локальный MCP-сервер

Изолированный учебный MCP-сервер на Java 25 и официальном MCP Java SDK. Использует `stdio`, не является частью `my-scents` и не обращается к REST API, MongoDB, Kafka, LLM или сети.

## Запуск

1. Установите JDK 25 и задайте `JAVA_HOME` в окружении процесса OpenCode.
2. В каталоге `mcp-server` выполните `./gradlew.bat installDist`.
3. Убедитесь, что рабочая конфигурация OpenCode содержит блок из [config/opencode.mcp.json.example](config/opencode.mcp.json.example).
4. Перезапустите OpenCode и выполните `opencode mcp list`: сервер `my-scents-local` должен иметь статус `connected`.
5. В чате OpenCode отправьте один из запросов из [evidence/ide-tool-trace.md](evidence/ide-tool-trace.md).

Клиент запускает `build/install/mcp-server/bin/mcp-server.bat` из `mcp-server`. Нельзя запускать сервер через Gradle: вывод Gradle нарушит MCP JSON-RPC в `stdout`.

`MCP_PROJECT_ROOT` задаётся только конфигурацией запуска и определяет trusted project root. Если переменная не задана, используется родительский каталог `mcp-server`. Локальные значения разрешено хранить в игнорируемом `.env`; пример содержит только имя переменной: [.env.example](.env.example). `JAVA_HOME` должен указывать на JDK 25 до запуска OpenCode: `opencode mcp list` подтвердит `my-scents-local connected`.

## Tools и контракт результата

| Tool | Параметры | Назначение |
| --- | --- | --- |
| `get_local_documentation` | `query`: string, 2-120 символов | Ищет только Markdown snapshot из `mcp-server/docs`, созданный при старте. |
| `search_project` | `query`: string, `path_prefix?`: string, `max_results?`: integer 1-50 | Ищет allowlisted текстовый snapshot внутри `MCP_PROJECT_ROOT`, созданный при старте. |
| `run_project_check` | `check`: enum `mcp_tests` | Запускает единственную фиксированную локальную команду Gradle для MCP-модуля. |

Каждый tool возвращает короткое текстовое `content` и `structuredContent`. На protocol boundary сервер отклоняет отсутствующие обязательные поля, лишние поля, неверные JSON-типы и значения за пределами объявленной схемы до вызова реализации tool; ошибка имеет тот же структурированный контракт.

```json
{"status":"success","tool":"search_project","data":{"query_length":10,"matches":[{"path":"src/ToolResult.java","line":6}]}}
```

При ошибке `structuredContent` имеет вид:

```json
{"status":"error","tool":"search_project","error":{"code":"ACCESS_DENIED","message":"Requested project path is not available."}}
```

Контракт реализован в [ToolResult.java](src/main/java/ru/my/scents/mcp/ToolResult.java). Поисковые tools возвращают только `query_length`, относительный путь, номер строки и флаг `truncated`, без query и содержимого файла. Ошибки не раскрывают stack trace, абсолютные пути, содержимое исключённых файлов и полные команды. Ненулевой код завершения `run_project_check` возвращается как `CHECK_FAILED`, а не как успешный результат.

Политика доступа отклоняет абсолютные и traversal-пути, platform-invalid paths, symbolic links, `.git`, `.idea`, `.gradle`, build outputs, dependency folders, `.venv`, `.ssh`, `.env*`, ключи/сертификаты, binary-файлы, а также файлы с именами, содержащими `credential`, `secret`, `password`, `token`, `id_rsa`, `private_key`, `private-key`, `api_key` или `api-key`. Файлы свыше 128 KiB исключаются. Во время запуска сервер canonical-path проверяет и открывает разрешённые файлы с `NOFOLLOW_LINKS`, ограничивая чтение 128 KiB, и строит неизменяемый in-memory snapshot. Snapshot ограничен 2 000 доступными filesystem entries, 10 000 строками и 4 MiB индексированных UTF-8 данных; результат устанавливает `truncated=true`, если любой лимит достигнут. Вызовы tools не открывают файлы и не обходят каталоги, поэтому подмена пути после старта процесса не меняет результаты. `MCP_PROJECT_ROOT` и `docs` должны оставаться неизменяемыми во время initial scan: portable Java NIO на Windows не предоставляет descriptor-relative обход, который атомарно защищает от локального процесса, подменяющего родительский каталог symbolic link в этот момент. Изменения становятся доступны только после перезапуска MCP-сервера. Поиск ограничен 50 совпадениями, 240 символами пути и первыми 500 символами строки. Проверка проекта использует фиксированный argument list, timeout пять минут и общий лимит 8 KiB для объединённого вывода. На Windows она создаётся через `CreateProcessW` в suspended state, получает Job Object с `KILL_ON_JOB_CLOSE` до `ResumeThread`, поэтому parent и его descendants охвачены containment до исполнения. Job закрывается при timeout, `OUTPUT_LIMIT_EXCEEDED`, interruption, ошибке reader и штатном завершении; ошибки native containment возвращают `PROCESS_UNAVAILABLE`. При превышении лимита сервер не передаёт child-process output MCP-клиенту; ожидание output reader также имеет ограниченный grace period.

## Подключение клиентов

Поддерживается только OpenCode. Точные шаги находятся выше в разделе «Запуск», рабочий пример конфигурации -- [config/opencode.mcp.json.example](config/opencode.mcp.json.example). Фактическая конфигурация репозитория -- [../opencode.json](../opencode.json). Интеграции с другими IDE и их конфигурации не предоставляются.

## Проверка

Из `mcp-server` выполните:

```powershell
./gradlew.bat test
./gradlew.bat build
./gradlew.bat installDist
```

`StdioServerSmokeTest` запускает distribution launcher с JDK 25, выполняет `initialize`, `tools/list`, два вызова `get_local_documentation` и два вызова `search_project`. Он проверяет JSON-RPC-only stdout, опубликованные input schemas, автоматическое отклонение некорректных аргументов, включая недопустимое значение `run_project_check`, до запуска handler, ограниченное ожидание stdout и безопасные stderr-логи. Smoke-test не запускает вложенный Gradle process: успешная маршрутизация валидного `run_project_check` через handler с fake process boundary покрыта `ProjectCheckToolTest`, а Windows containment -- `WindowsProcessJobIntegrationTest`. Фактические вызовы агентом OpenCode и пять проверочных запросов сохранены в [evidence/ide-tool-trace.md](evidence/ide-tool-trace.md).

На Windows `WindowsProcessJobIntegrationTest` запускает реальный JVM helper, который создаёт долгоживущий child process. После закрытия Job тест ожидает завершение обоих зафиксированных PID.
