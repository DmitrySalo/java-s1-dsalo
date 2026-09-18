# Архитектура Ароматеки

## Контекст и источники истины

`my-scents` -- единственный прикладной модуль Gradle multi-project `java-s1-dsalo`. Это модульный монолит на Java 25 и Spring Boot 3.5.6: MongoDB хранит данные, REST и gRPC принимают запросы, Kafka передаёт события. Фактическое состояние описывает [ARCHITECTURE.md](../../ARCHITECTURE.md).

Приоритет правил: задача пользователя, ближайший `AGENTS.md`, корневой `AGENTS.md`, затем этот файл. Перед изменением прочитай затрагиваемые порт, адаптер и use case; не применяй в проекте шаблоны другого стека.

## Разрешённый поток зависимостей

```text
adapter.controller (HTTP/gRPC/Kafka) -> boundary.usecase -> domain.usecase
domain.usecase -> boundary.repository | boundary.gateway | boundary.event
adapter.repository | adapter.gateway | adapter.event.publisher -> infra
```

- `boundary` содержит входные/выходные порты и команды `Create...Param` / `Update...Param`.
- `domain.entity` содержит агрегаты, value objects и enum. Инварианты живут здесь, не в контроллере или Mongo model.
- `domain.usecase` оркестрирует сценарий через порты. Не передавай сюда HTTP DTO, `MongoTemplate`, Kafka API или generated Protobuf.
- `adapter.controller.http` преобразует request DTO в boundary-команду, вызывает use case, затем маппит результат в response DTO.
- `adapter.repository` реализует порт через `MongoTemplate`; document model и converter остаются в этом адаптере.
- `adapter.gateway.grpc` изолирует blocking gRPC stub и преобразование Proto <-> domain.
- `adapter.event.publisher` и `adapter.event.consumer` изолируют Kafka и Protobuf от HTTP и repository-слоёв.
- `infra` содержит конфигурацию и технические компоненты.

Существующие use case уже являются Spring `@Service`, используют `infra.logger.Logger`, а fragrance use case знает generated `FragranceEvent`. Это документированные отступления от строгой Clean Architecture. Не распространяй их на новый код: добавляй новые зависимости через `boundary`.

## Типовой CRUD

Для нового поля существующего агрегата меняй слои в таком порядке:

1. Добавь или измени value object/enum и инварианты в `domain.entity`.
2. Добавь поле в `Create...Param` / `Update...Param`, domain converter и use case.
3. Обнови Mongo DB model и converter repository-адаптера.
4. Если поле доступно по REST, измени отдельные request/response DTO и converters контроллера.
5. Если поле участвует в gRPC или Kafka, выполни правила `api-contracts.md`.
6. Добавь тесты на инвариант, mapping и затронутую интеграцию.

Не вызывай `MongoTemplate` из контроллера и не передавай `CreateFragranceRequest` в `FragranceUseCase`:

```java
CreateFragranceParam param = RequestFragranceConverter.createRequestToModel(request);
Fragrance fragrance = fragranceUseCase.create(param);
return ResponseEntity.ok(ResponseFragranceConverter.createResultToResponse(fragrance));
```

## Интеграции

- `FragranceUseCase.get` сначала читает MongoDB, затем при отсутствии вызывает `FragranceGateway`; успешный внешний ответ сохраняется локально.
- У внешнего gRPC уже есть deadline и retry в `grpc.fragrance-service`. Не добавляй второй бесконтрольный retry в use case.
- `create`, `update`, `delete` парфюма меняют MongoDB, затем публикуют Protobuf-событие. Это не outbox-операция: не заявляй exactly-once гарантию.
- Kafka listener читает raw Protobuf bytes из `outer-fragrance-events`; retry topics и DLT уже настраиваются Kafka-конфигурацией.
- Обработчик события должен быть идемпотентным и извлекать ID сущности из `payload`, а не `event_id`: `event_id` идентифицирует событие.

Не выполняй gRPC/Kafka вызов внутри Mongo-транзакции. Если требование нуждается в гарантированной согласованности MongoDB и Kafka, предложи transactional outbox, модель хранения события и план миграции до реализации.

## Границы изменений

- Не выделяй микросервис, новый Gradle-модуль или отдельную БД без независимого владения данными, релиза или масштабирования.
- Не меняй public REST/gRPC/Kafka контракт, порт, security-политику или схему данных как побочный эффект рефакторинга.
- Не добавляй JPA, SQL/Flyway или новую библиотеку, если задача решается `MongoTemplate`, Protobuf и Spring-компонентами.
- Не исправляй известные ограничения из `ARCHITECTURE.md` вне задачи, но учитывай их при связанных изменениях.

## Эксплуатация

Значения из `application.yml`: REST `8087`, management `8086`, inbound gRPC `50053`, внешний fragrance gRPC `localhost:50051`. Actuator доступен как `/health`, `/metrics`, `/prometheus` на management-порту. В Dockerfile и Compose есть документированное расхождение с этими портами; не используй их как источник истины.

При изменении архитектурного решения обновляй `ARCHITECTURE.md`; при изменении пользовательского поведения -- `README.md`. Для каждого задания создай `promts/<kebab-case-name>.md` с полным текстом задания и добавь в конец `REPORT.md` запись с датой, фактическим результатом и ссылкой на этот файл. Подробный обязательный формат определяет корневой `AGENTS.md`.
