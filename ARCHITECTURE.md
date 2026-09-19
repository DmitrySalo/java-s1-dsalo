# Архитектура проекта «Ароматека»

**Статус:** описывает фактическую реализацию в репозитории на 2026-09-18.
**Система:** Gradle multi-project `java-s1-dsalo`; прикладной модуль `my-scents`; артефакт `ru.my.scents:my-scents:0.0.1-SNAPSHOT`.

## Назначение

«Ароматека» задумана как персональная картотека парфюмов. Реализованный backend управляет карточками пользователей и парфюмов, сохраняет их в MongoDB, выдаёт данные по REST и gRPC и обменивается событиями об изменениях парфюмов через Kafka. При отсутствии локальной карточки приложение запрашивает её у внешнего gRPC-сервиса и сохраняет ответ локально.

Это модульный монолит: прикладные части выполняются одним Spring Boot-процессом, используют одну MongoDB и поставляются одним артефактом. Kafka, Schema Registry и внешний fragrance-service -- интеграции, а не сервисы данного кода.

## Принципы и паттерны

- **Clean Architecture:** транспорт, сценарии, доменная модель, хранение и технические детали разделены пакетами.
- **Dependency Inversion:** сценарии зависят от портов `boundary`; MongoDB, Kafka и gRPC реализуют их через адаптеры.
- **Ports and Adapters / Hexagonal Architecture:** REST, входящий gRPC и Kafka listener -- входные адаптеры; MongoDB, исходящий gRPC и Kafka producer -- выходные.
- **DDD tactical patterns:** `User` и `Fragrance` -- сущности (entity); ID, имя, email, телефон, рейтинг и описание -- value objects. Текущие ограниченные контексты: `user` и `fragrance`.
- **Явные контракты:** HTTP DTO не являются доменными сущностями; gRPC и Kafka-контракты заданы Protobuf-файлами.
- **Наблюдаемость и устойчивость:** JSON-логи с MDC, Actuator/Micrometer, Mongo/gRPC-метрики, gRPC deadline и retry, Kafka retry topics и DLT.
- **Эволюционность:** модульный монолит является текущей единицей поставки; отдельный сервис следует выделять только при независимом владении данными и жизненном цикле. Исключение для `langchain-agent`: это учебный внешний клиент публичного API, а не владелец доменных данных и не выделенный bounded context.

### Фактические отступления от строгой Clean Architecture

- `domain.usecase.*` напрямую зависит от `infra.logger.Logger`.
- `FragranceUseCaseImpl` и его event converter знают generated Protobuf `FragranceEvent`.
- Реализации use case помечены `@Service` и зависят от Spring.

Следовательно, `domain` пока не является framework-independent слоем в строгом смысле. Новые сценарии следует вводить через порты `boundary` и не расширять зависимость доменной логики от Spring, Protobuf, HTTP, MongoDB или Kafka.

## Границы и поток данных

```text
REST client ─┐
gRPC client ─┼─> inbound adapter ─> boundary.usecase ─> domain.usecase
Kafka topic ─┘                                      │
                                                    ├─> boundary.repository ─> MongoDB adapter ─> MongoDB
                                                    ├─> boundary.gateway ────> gRPC adapter ────> external service
                                                    └─> boundary.event ──────> Kafka adapter ───> Kafka topic
```

`UserController` преобразует HTTP request в команду, вызывает `UserUseCase`, затем маппит агрегат в response. `UserUseCaseImpl` работает через `UserRepository`, а его реализация сохраняет Mongo document через `MongoTemplate`.

`FragranceUseCaseImpl.create` и `update` сохраняют карточку, формируют Protobuf `FragranceEvent` и асинхронно публикуют его в `my-scents-fragrance-events`. `get` сначала ищет MongoDB, затем вызывает `FragranceGateway` (blocking gRPC-клиент) и сохраняет успешный ответ. `delete` удаляет Mongo document и публикует delete-event.

`FragranceEventListener` читает `outer-fragrance-events`, а `DefaultFragranceEventHandler` направляет типы `CREATED`, `UPDATED`, `DELETED` в соответствующие use case. Для listener настроены экспоненциальные retry topics и DLT. Событие содержит `event_id`, `event_type`, `event_timestamp`, `payload`.

Прикладные producer/listener передают raw Protobuf bytes. Зависимость Confluent serializer и контейнер Schema Registry присутствуют, но автоматически в прикладной сериализации не используются.

## Структура проекта

```text
.
├── settings.gradle                         # Корень Gradle multi-project
├── docker-compose.yaml                     # Локальная инфраструктура
├── my-scents/
│   ├── build.gradle                        # Плагины, зависимости, Protobuf generation
│   ├── Dockerfile                          # Многоступенчатая контейнерная сборка
│   ├── src/main/java/ru/my/scents/
│   │   ├── MyScentsApplication.java        # Точка входа Spring Boot
│   │   ├── boundary/                       # Порты и команды use case
│   │   ├── domain/                         # Агрегаты, value objects, use cases
│   │   ├── adapter/                        # Входные и выходные адаптеры
│   │   ├── infra/                          # Техническая конфигурация и реализации
│   │   └── shared/                         # Общие converters
│   ├── src/main/proto/fragrance/           # gRPC и Kafka Protobuf contracts
│   ├── src/main/resources/                 # Spring и Logback configuration
│   ├── src/test/                           # Тесты, stubs, Testcontainers config
│   └── gripmock/fragrance/stubs/           # Mock-сценарии gRPC
├── langchain-agent/                         # Отдельный Python/LangChain REST-клиент my-scents
│   ├── app/                                 # FastAPI, policy, tool и HTTP-клиент
│   ├── prompts/                             # Trusted prompts агента
│   ├── tests/                               # Изолированные Python-тесты
│   └── examples/                            # Результаты контрольных проверок
└── scripts/
    ├── schema-registry/                    # Регистрация Protobuf schemas
    └── kafka/                              # Python-инструменты Kafka
```

### Назначение пакетов

| Пакет | Назначение |
| --- | --- |
| `boundary.usecase` | Входные порты `UserUseCase`, `FragranceUseCase`. |
| `boundary.model` | Команды создания и обновления, отделённые от HTTP DTO. |
| `boundary.repository` | Выходные порты хранения агрегатов. |
| `boundary.gateway` | Выходной порт внешнего поиска сущности/агрегата. |
| `boundary.event` | Выходной порт публикации события сущности/агрегата. |
| `domain.entity` | Сущности/агрегаты, ID, name/rating/resume value objects и enums характеристик. |
| `domain.usecase` | Оркестрация CRUD-сценариев и доменные преобразования. |
| `adapter.controller.http` | REST CRUD, DTO, converters и error resolvers. |
| `adapter.controller.grpc` | Входящая реализация `FragranceService.GetFragrance`. |
| `adapter.gateway.grpc` | Blocking gRPC-клиент и Protobuf-to-domain mapping. |
| `adapter.repository` | MongoDB-реализации портов, document models и mapping. |
| `adapter.event.publisher` | Kafka producer внутреннего потока. |
| `adapter.event.consumer` | Kafka listener, retry/DLT и event-to-command mapping. |
| `infra.db.mongodb` | `MongoClient`, `MongoTemplate` и Mongo-метрики. |
| `infra.grpc` | Настройки канала, retry, метрик и вручную запущенного gRPC-сервера. |
| `infra.kafka` | Producer/consumer factories, raw Protobuf serializer/deserializer, topic properties. |
| `infra.http` | HTTP filter/interceptor и MDC-корреляция запросов. |
| `infra.logger` | Интерфейс логгера, SLF4J-реализация и асинхронная очередь. |
| `shared.converter` | Преобразование timestamp и enum домена/Protobuf. |

## Сервис LangChain-агента

`langchain-agent` -- самостоятельный Python 3.11+ сервис на LangChain, LangChain Ollama, FastAPI, HTTPX, Pydantic и Uvicorn. Он является потребителем публичного REST API `my-scents`, не владеет доменными данными и не меняет границы модульного монолита.

Агент получает `MY_SCENTS_BASE_URL`, `OLLAMA_BASE_URL` и `OLLAMA_MODEL` только из окружения или локального `.env`; `.env` не коммитится. Интеграционная граница ограничена `POST`, `GET` и `PUT /api/v1/fragrances`. У сервиса нет доступа к MongoDB, Kafka, Schema Registry, gRPC, Docker socket или внутренним Java-пакетам.

Программный allowlist в HTTP-клиенте и tool запрещает произвольные URL, методы, shell и файловую систему. `fragrance_api` подключён к LangChain `create_agent`, но его выполнение требует временного разрешения программного policy layer. Create/update возвращают `confirmation_required` с одноразовым `prepared_operation_id`, действительным пять минут, и выполняются лишь при `confirmed=true` с этим ID. Tool выводит в stderr логи `TOOL_CALL` и `TOOL_RESULT`, содержащие только operation, method, path и безопасный resource ID, без body, токенов, URI с credentials и персональных данных. `GET /health` проверяет agent-service и не раскрывает состояние `my-scents`. До завершения TD-5 сервис разрешён только для локальной учебной среды.

### Сценарии работы агента

| Сценарий | Действие агента | API-вызов |
| --- | --- | --- |
| Создать карточку | Формирует ограниченный payload, возвращает подготовленную операцию, ждёт подтверждения | После подтверждения `POST /api/v1/fragrances` |
| Получить карточку | Валидирует UUID и вызывает tool | `GET /api/v1/fragrances?fragranceId=...` |
| Обновить карточку | Формирует полный поддерживаемый payload и ждёт подтверждения | После подтверждения `PUT /api/v1/fragrances` |
| Справка | Возвращает допустимые возможности | Нет tool-вызова |
| Ошибка API | Возвращает безопасный контракт без неконтролируемого retry | Нет повторного вызова |

```text
Natural-language request
  -> LangChain agent
  -> parse intent and validate tool input
  -> confirmation_required
  -> explicit confirmation
  -> LangChain tool
  -> MyScentsClient
  -> public REST API my-scents
  -> normalized agent response
```

### Структура LangChain-сервиса

| Компонент | Назначение |
| --- | --- |
| `app/main.py` | HTTP-вход FastAPI: health и команда агента. |
| `app/cli.py` | CLI-вход той же прикладной логики. |
| `app/agent/factory.py` | Собирает `ChatOllama` и LangChain `create_agent`. |
| `app/agent/tools.py` | Единственный allowlisted HTTP tool. |
| `app/agent/response.py` | Нормализует обязательный контракт ответа. |
| `app/agent/service.py` | Программно применяет confirmation policy. |
| `app/my_scents/client.py` | HTTP-клиент публичного REST API. |
| `app/my_scents/models.py` | Ограничивает поля JSON и enum. |
| `prompts/*.md` | Trusted system prompt и примеры запросов. |
| `examples/verification-results.md` | Контрольные результаты и безопасные traces. |

## Доменные данные

`User`: `id`, `firstName`, `lastName`, `email`, `phoneNumber`, `createdAt`, `updatedAt`. ID проверяется как UUID v4; email нормализуется в lower case; телефон принимает ровно 11 цифр.

`Fragrance`: `id`, `name`, `rating` (0..10), `resume`, `concentration`, набор `type`, `gender`, набор `season`, `longevity`, `sillage`, `availability`, `createdAt`, `updatedAt`. Допустимые перечисления описаны в `domain/entity/fragrance` и дублируются в Protobuf.

MongoDB используется без Spring Data repositories: реализации портов вызывают `MongoTemplate` напрямую. `save` имеет семантику upsert; `delete` бросает `IllegalStateException`, если document не найден.

## Контракты

### REST

| Операция | HTTP | Путь | Параметры |
| --- | --- | --- | --- |
| Создать пользователя | `POST` | `/api/v1/users` | JSON `firstName`, `lastName`, `email`, `phoneNumber` |
| Обновить пользователя | `PUT` | `/api/v1/users` | JSON update DTO |
| Получить/удалить пользователя | `GET` / `DELETE` | `/api/v1/users` | `userId` как query parameter |
| Создать парфюм | `POST` | `/api/v1/fragrances` | JSON характеристик парфюма |
| Обновить парфюм | `PUT` | `/api/v1/fragrances` | JSON update DTO |
| Получить/удалить парфюм | `GET` / `DELETE` | `/api/v1/fragrances` | `fragranceId` как query parameter |

Контроллеры возвращают `200 OK` для create, update, get и delete. Request-level annotations `@Valid` есть, но records не содержат Jakarta Validation constraints; фактическая проверка многих инвариантов выполняется value objects в domain.

### gRPC и Protobuf

`src/main/proto/fragrance/fragrance_service.proto` определяет `FragranceService.GetFragrance(GetFragranceRequest) returns (GetFragranceResponse)`. `FragranceData` включает все характеристики и timestamps. Входящий сервер запускается вручную на `grpc.server.port`; внешний blocking клиент использует `grpc.fragrance-service.host` и `port`.

### Безопасность API

По умолчанию применяется Spring Security auto-configuration. Явный профиль `local` определяет отдельную `SecurityFilterChain`, отключает CSRF и разрешает unauthenticated requests только для изолированной учебной проверки LangChain-агента; он не включается по умолчанию и не предназначен для production. Перед production необходимо утвердить аутентификацию, resource-level authorization по владельцу карточки, защиту management endpoints, TLS для gRPC/Kafka/MongoDB и ограничения входных payload.

## Технологии и версии

Версия указана, если она зафиксирована в `build.gradle`, Gradle Wrapper, Dockerfile или Compose. Управляемые Spring Boot зависимости наследуют версию из BOM Spring Boot 3.5.6.

| Технология | Версия | Назначение |
| --- | --- | --- |
| Java | 25 | Язык и toolchain. |
| Gradle Wrapper | 9.1.0 | Сборка и запуск задач. |
| Spring Boot | 3.5.6 | Приложение, автоконфигурация и dependency BOM. |
| Spring dependency-management plugin | 1.1.7 | Управление транзитивными версиями. |
| Spring Boot Gradle plugin | 3.5.6 | Сборка исполняемого Boot JAR. |
| Protobuf Gradle plugin | 0.9.5 | Генерация Java/gRPC-кода из `.proto`. |
| Spring MVC/Web, Validation | управляется Boot | REST API и проверка запросов. |
| Jetty | управляется Boot | Встроенный HTTP-сервер вместо Tomcat. |
| Spring Data MongoDB | управляется Boot | Mongo driver, `MongoTemplate`, mapping. |
| MongoDB Docker image | `mongo:8-noble` | Локальное хранилище. |
| Spring Kafka | управляется Boot | Producer, consumer, retry topics, DLT. |
| Apache Kafka Docker image | Confluent Platform 7.6.0 | Локальный broker в KRaft-режиме. |
| Confluent Schema Registry | Confluent Platform 7.6.0 | Локальный реестр Protobuf schemas. |
| Confluent Kafka Protobuf serializer | 8.1.1 | Подключён, но прикладным кодом не выбран. |
| gRPC Java | 1.71.0 | Клиент, сервер, stubs и code generation plugin. |
| Spring gRPC starter | 1.0.0-RC1 | Интеграция Spring и gRPC; server auto-config отключена. |
| Protocol Buffers | 4.33.5 | Контракты, protoc и Java runtime. |
| GripMock | `bavix/gripmock:latest` | Локальный mock внешнего gRPC-сервиса. |
| Spring Retry / Spring Aspects | управляется Boot | Kafka retry topics и механизмы retry. |
| Spring Boot Actuator | управляется Boot | Health, metrics и Prometheus endpoint. |
| Micrometer Prometheus registry | управляется Boot | Экспорт метрик Prometheus. |
| Logback | управляется Boot | Логирование. |
| Logstash Logback Encoder | 9.0 | JSON-логи с MDC в stdout. |
| Lombok | управляется Boot | Генерация boilerplate кода. |
| MapStruct | 1.6.3 | Compile-time mapping. |
| Jakarta EL | 4.0.2 | Реализация Expression Language для validation. |
| Testcontainers | 2.0.2 | Контейнеры в тестах. |
| Testcontainers MongoDB/JUnit Jupiter | 1.21.3 | MongoDB Testcontainers и JUnit 5 integration. |
| REST Assured | 5.5.6 | Зависимость для HTTP-тестов. |
| AssertJ | 3.27.6 | Fluent assertions. |
| JSON Unit AssertJ | 5.1.0 | Сравнение JSON в тестах. |

Образ сборки Docker использует `gradle:9.3.0-jdk25`, а Wrapper -- Gradle 9.1.0. Это различие следует устранить или обосновать для воспроизводимых сборок.

## Конфигурация, развёртывание и наблюдаемость

`application.yml` задаёт REST `8087`, management `8086`, inbound gRPC `50053`; management base path -- `/`, открыты `health`, `metrics`, `prometheus`. HTTP Jetty ограничен 200 соединениями и использует graceful shutdown. MongoDB URI, Kafka bootstrap servers, gRPC host/port и другие значения должны передаваться для каждого окружения внешней конфигурацией.

`docker-compose.yaml` поднимает MongoDB, Kafka, Schema Registry, одноразовую регистрацию схем, Kafka UI, GripMock и опциональный `my-scents-app`. Dockerfile создаёт non-root runtime user, но включает JMX без authentication и TLS: такую настройку нельзя открывать за пределы локальной изолированной среды.

Имеются JSON-логи с MDC, HTTP interceptor/filter, HTTP server metrics, Mongo command/pool metrics и gRPC client metrics. gRPC-вызов ограничен deadline 60 секунд; retry задан максимумом 10 попыток. Kafka producer использует `acks=all`, 3 retry; consumer отключает auto-commit и обрабатывает до 100 записей за poll.

## Тестирование

- `UserRepositoryTests` -- MongoDB repository пользователя.
- `FragranceRepositoryTests` -- MongoDB repository парфюма.
- `FragranceProtoConverterTests` -- преобразование domain/proto gRPC.
- `configuration` и `stub` -- базовые классы, Testcontainers-конфигурация и тестовые объекты.

Покрытие не включает REST-контроллеры, обработчики ошибок, use cases, Kafka producer/consumer/retry/DLT и gRPC server/client.

## Текущие ограничения и технический долг

| Область | Факт и риск | Рекомендуемое действие |
| --- | --- | --- |
| Docker ports | Compose и Dockerfile ожидают HTTP `8080` и management `8081`, но application config задаёт `8087` и `8086`; health check использует несуществующий `/manage/health`. | Согласовать порты, expose и health check, затем добавить container smoke test. |
| События | Публикация в Kafka происходит после записи в MongoDB, но без транзакции/outbox. Возможна рассинхронизация БД и события при сбое. | При критичности интеграции внедрить transactional outbox и идемпотентный consumer. |
| Kafka consumer | Обработчик входящего delete-event передаёт `event_id` как ID парфюма, а не ID из payload. Create/update через use case публикуют новые события. | Исправить mapping delete, исключить feedback loop и покрыть event flow интеграционными тестами. |
| Контракты | REST create/update имеют одинаковые `200 OK`; нет OpenAPI и явной версии событий. | Зафиксировать API contract, коды ответов и стратегию эволюции Protobuf. |
| Security | Нет явной конфигурации авторизации, ownership и production transport security. | Определить SecurityFilterChain, модель субъектов, ресурсные проверки и защищённую конфигурацию окружений. |
| Тесты | Нет HTTP/Kafka/gRPC/integration coverage. | Добавить тесты для ошибок, интеграций, retry/DLT и container startup. |

## Правила развития

- Не передавать HTTP DTO, Mongo document или generated Protobuf types за границу адаптера без необходимости.
- Изменять `.proto` и публичные REST DTO обратно-совместимо: новые поля добавлять как опциональные, не переиспользовать номера полей Protobuf.
- Не выполнять сетевые вызовы внутри будущих транзакций MongoDB и не публиковать бизнес-событие до успешной фиксации данных.
- Хранить секреты, URI с учётными данными и JMX credentials вне репозитория; не логировать персональные данные, токены или тела запросов.
- Перед выделением сервиса доказать необходимость отдельного владения данными, независимого релиза или масштабирования.
