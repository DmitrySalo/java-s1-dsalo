# Инструкции для модуля `my-scents`

Эти правила уточняют корневой `AGENTS.md` для единственного прикладного модуля проекта. При конфликте действуют приоритеты из корневого файла: явные требования пользователя, этот файл, корневой `AGENTS.md`, `ARCHITECTURE.md`, тематические инструкции и существующий код.

## Перед началом задачи

1. Для изменения существующего кода или документации либо новой доработки обнови локальную `main` из удалённого репозитория, создай от неё отдельную лаконичную ветку `feature/<kebab-case-name>` и работай только в ней. При чужих или незавершённых изменениях, блокирующих эту операцию, не меняй ветки и запроси указания пользователя.
2. Прочитай затронутый код, тесты и конфигурацию, не делай предположений по шаблонам другого проекта.
3. Прочитай `.opencode/instructions/architecture.md` для любого изменения структуры, сценария или интеграции.
4. Прочитай дополнительные инструкции по области изменения:

| Область | Обязательный файл |
| --- | --- |
| Java, Spring-компоненты, mapping, ошибки, логи | `.opencode/instructions/java-style.md` |
| MongoDB document, `MongoTemplate`, query, index, данные | `.opencode/instructions/database.md` |
| REST DTO/endpoint, gRPC/Protobuf, Kafka event | `.opencode/instructions/api-contracts.md` |
| Тесты | `.opencode/instructions/testing.md` |
| Внешний ввод, секреты, security, TLS, Actuator | `.opencode/instructions/security.md` |

Если задача создаёт риск потери MongoDB-данных, несовместимого изменения REST/Protobuf/Kafka контракта, утечки персональных данных или ослабления security, сначала опиши риск и безопасный вариант. Не выполняй destructive изменение без явного требования пользователя.

## Фактический стек

- Java toolchain `25`, Gradle Wrapper `9.1.0`, Spring Boot `3.5.6`.
- MongoDB через `MongoTemplate`; Spring Data repository, JPA, SQL и Flyway в модуле не используются.
- REST на Jetty: `8087`; Actuator на `8086`; inbound gRPC: `50053`.
- gRPC Java, Protocol Buffers и Spring gRPC starter; Protobuf исходники: `src/main/proto/fragrance`.
- Kafka producer/consumer передают raw Protobuf bytes; listener имеет retry topics и DLT.
- Тесты: JUnit Jupiter, AssertJ, Spring Boot Test, Testcontainers MongoDB, REST Assured, `spring-kafka-test`, `grpc-testing`.

Не добавляй JPA, Flyway, PostgreSQL, Spring Data repository, frontend-стек или новую зависимость, когда задача решается компонентами модуля и JDK.

## Структура и зависимости

Соблюдай следующий поток:

```text
adapter.controller (HTTP/gRPC/Kafka) -> boundary.usecase -> domain.usecase
domain.usecase -> boundary.repository | boundary.gateway | boundary.event
adapter.repository | adapter.gateway | adapter.event.publisher -> infra
```

- `boundary` -- порты и команды `Create...Param` / `Update...Param`.
- `domain.entity` -- агрегаты `User` / `Fragrance`, value objects и enum; здесь проверяются доменные инварианты.
- `domain.usecase` -- оркестрация через порты.
- `adapter.controller.http` -- request/response DTO и их mapping; controller не содержит business logic и не вызывает repository.
- `adapter.repository` -- Mongo DB model, converter, `MongoTemplate`-реализация порта.
- `adapter.gateway.grpc` -- внешний blocking gRPC client и Proto-to-domain mapping.
- `adapter.event.publisher` / `adapter.event.consumer` -- Kafka и event mapping.
- `infra` -- конфигурация MongoDB, gRPC, Kafka, HTTP, логирования и метрик.

Не передавай HTTP DTO, Mongo document или generated Protobuf type за границу соответствующего адаптера. Новые сценарии вводи через `boundary`, не расширяя существующие отступления: текущие use case являются Spring `@Service`, используют `infra.logger.Logger`, а `FragranceUseCaseImpl` знает `FragranceEvent`.

## Типовые изменения

### Поле доменной сущности

Изменяй последовательно: value object/enum и entity -> `Create...Param`/`Update...Param` -> domain converter/use case -> Mongo DB model и repository converter -> REST DTO/converter -> Proto/event converter, если поле есть в интеграционном контракте -> тесты.

```java
CreateFragranceParam param = RequestFragranceConverter.createRequestToModel(request);
Fragrance fragrance = fragranceUseCase.create(param);
return ResponseEntity.ok(ResponseFragranceConverter.createResultToResponse(fragrance));
```

Не вызывай `MongoTemplate` из controller-а и не передавай `CreateFragranceRequest` в use case.

### MongoDB

`save` имеет upsert-семантику. При добавлении поля определи чтение старых documents без него: nullable значение, доменный default или согласованная миграция данных. Для нового query используй `MongoTemplate` с параметризованными `Query`/`Criteria`, ограничивай выдачу и добавляй индекс только под подтверждённый query pattern. Не выполняй gRPC/Kafka вызов внутри Mongo-транзакции.

### REST

Существующие CRUD endpoint-ы: `/api/v1/users` и `/api/v1/fragrances`. `GET`/`DELETE` принимают `userId`/`fragranceId` как query parameter. Все текущие успешные операции, включая create/update/delete, возвращают `200 OK`; delete не имеет тела. Это публичный контракт, не меняй его без задачи и плана совместимости.

Добавляй отдельные request/response DTO и converter; применяй `@Valid` к body и Bean Validation к query parameter. Помни: `@Valid` не даёт проверки record без validation-аннотаций; бизнес-инварианты проверяют domain value objects.

### gRPC, Protobuf и Kafka

Не меняй и не переиспользуй номера, типы или семантику существующих Protobuf-полей. Новое поле добавляй с новым номером, обновляй все producer, consumer и converters, затем выполняй `:my-scents:generateProto` или test/build. Generated Java sources не редактируй.

Kafka event состоит из `event_id`, `event_type`, `event_timestamp`, `payload`. `event_id` идентифицирует сообщение, ID парфюма -- `payload.id`. Listener обязан безопасно обрабатывать неизвестный тип, неполный payload, невалидный enum и повторную доставку. Не скрывай exception в listener-е: это обходит retry/DLT. Не публикуй исходящее событие при обработке входящего, если это создаёт feedback loop.

`FragranceUseCase.get` читает MongoDB и только при отсутствии вызывает внешний gRPC gateway, после чего сохраняет успешный ответ. В gRPC-конфигурации уже есть deadline и ограниченный retry; не добавляй неконтролируемый retry поверх него.

## Безопасность и наблюдаемость

`User.email` и `User.phoneNumber` -- персональные данные. Не логируй их, request body, protobuf bytes, Mongo URI с учётными данными, токены, cookie или stack trace в API-ответе. Логируй через `infra.logger.Logger` безопасные ID и типы событий.

Spring Security подключён, но `SecurityFilterChain`, users и authorization rules отсутствуют. Не заявляй, что API уже защищён, и не добавляй authentication/authorization, CORS, TLS или Actuator exposure без оценки совместимости и тестов. Перед production management endpoint-ы, MongoDB, Kafka и gRPC требуют утверждённой защиты; JMX в Dockerfile нельзя открывать за пределы изолированной локальной среды.

## Проверка и документация

Из корня репозитория запускай релевантные команды:

```powershell
./my-scents/gradlew.bat :my-scents:test
./my-scents/gradlew.bat :my-scents:build
```

- Для value object/converter/use case добавляй unit-тест без Spring context.
- Для MongoDB mapping/query добавляй repository integration test с Testcontainers.
- Для REST добавляй HTTP integration test c `BaseIntegrationTest` и REST Assured.
- Для gRPC/Kafka не подключайся к локальной инфраструктуре разработчика: используй stub/fake или test broker/server.
- Не используй `Thread.sleep`; проверяй асинхронность ограниченным polling или средствами test broker.

Перед завершением выполни релевантные тесты, сообщи точные команды и результат, добавь изменения в Git без commit. При изменении архитектуры обнови `ARCHITECTURE.md`; при изменении пользовательского поведения/публичного контракта обнови `README.md`.

Для каждого полученного задания создай отдельный файл `promts/<kebab-case-name>.md` с полным текстом задания до завершения работы. Затем добавь в **конец** `REPORT.md` одну запись в хронологическом порядке: дата, краткий фактический результат и ссылка формата `**Задание:** [<file>.md](promts/<file>.md).`. Не добавляй запись в начало или между существующими записями; не завершай задачу, если файл или ссылка отсутствуют. Добавь prompt-файл и `REPORT.md` в Git вместе с файлами задачи, но не создавай commit.
