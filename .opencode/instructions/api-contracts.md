# REST, gRPC и Kafka-контракты

## Существующие контракты

REST имеет базу `/api/v1` и использует query parameters для ID:

| Ресурс | Методы | ID для GET/DELETE |
| --- | --- | --- |
| Пользователь | `POST`, `PUT`, `GET`, `DELETE /api/v1/users` | `userId` |
| Парфюм | `POST`, `PUT`, `GET`, `DELETE /api/v1/fragrances` | `fragranceId` |

Текущие controller-ы возвращают `200 OK` для create/update/get/delete, delete -- без тела. Не меняй это на `201`/`204` в несвязанной задаче: это публичное изменение. gRPC контракт расположен в `my-scents/src/main/proto/fragrance`; `FragranceService.GetFragrance` принимает `fragrance_id`. Kafka event -- `FragranceEvent(event_id, event_type, event_timestamp, payload)` в `fragrance_event.proto`.

## REST: реализация и валидация

Для поля, доступного клиенту, используй отдельные request/response records в пакете конкретного controller-а и converter. Добавляй `@Valid` к `@RequestBody`; query parameter валидируй через `@Validated` на controller и Bean Validation-аннотацию.

```java
@GetMapping
public ResponseEntity<GetFragranceResponse> getFragrance(
        @NotBlank @RequestParam String fragranceId
) {
    return ResponseEntity.ok(ResponseFragranceConverter.getResultToResponse(
            fragranceUseCase.get(fragranceId)
    ));
}
```

В request DTO проверяй форму (blank, length, range, collection size), в value object -- бизнес-инвариант. `@Valid` не валидирует record без constraints. Не передавай domain entity или DB model прямо в JSON.

Новый endpoint добавляй только с request/response, безопасным mapping-ом ошибок и HTTP-тестом. OpenAPI в проекте отсутствует: не заявляй, что документация генерируется автоматически. При реальном изменении публичного контракта обновляй `README.md` и `ARCHITECTURE.md`.

## Protobuf и gRPC

- Не меняй номер, тип или семантику существующего поля и не переиспользуй удалённый field number.
- Добавляй поле с новым номером; producer заполняет его, consumer безопасно обрабатывает отсутствие поля.
- Не меняй `java_package` и не редактируй generated Java sources: их создаёт Gradle task `generateProto`.
- После `.proto` изменения выполни `:my-scents:generateProto` или полный test/build и обнови domain/proto converters с обеих сторон границы.

```proto
// Совместимое расширение: номер никогда ранее не использовался.
string source_name = 5;
```

## Kafka-события

- `event_id` идентифицирует сообщение, `payload` содержит данные сущности. Не подменяй ими друг друга.
- Событие сообщает о завершившемся факте: `CREATED`, `UPDATED`, `DELETED`; это не скрытая команда.
- Listener получает raw Protobuf bytes. Не переключай producer/consumer на Confluent serializer только потому, что зависимость присутствует: это несовместимое изменение формата.
- Consumer обязан переносить повторную доставку: перед записью определяй идемпотентное поведение, особенно для delete и повторного create/update.
- Не публикуй новое исходящее событие при обработке входящего, если это сформирует feedback loop. Текущая реализация требует отдельного исправления перед таким изменением.
- Для проблем обработки используй настроенные retry topics и DLT; не скрывай exception, иначе сообщение будет подтверждено как успешное.

Пример корректного delete mapping: ID удаляемого парфюма берётся из payload, если именно там его определяет контракт, а не из `event_id`.

```java
private Command delete() {
    return event -> fragranceUseCase.delete(event.getPayload().getId());
}
```

Перед изменением любого контракта укажи breaking-change риск, обнови все producer/consumer/converter/test и не удаляй старое поле без согласованного периода миграции.
