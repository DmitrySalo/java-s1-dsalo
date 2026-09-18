# Java и Spring Boot

## Стек и стиль

Пиши код модуля `my-scents` на Java 25 в существующем стиле: Lombok для конструкторов, `record` для DTO и команд, Spring constructor injection, MapStruct только там, где он уже применяется. В PowerShell используй Gradle Wrapper: `./my-scents/gradlew.bat :my-scents:<task>`.

- Не используй field injection, wildcard imports, `null` как нормальный результат и `Optional.get()`.
- `Impl` допустим для реализаций портов и use case, как принято в модуле; не добавляй бессодержательные `Helper`, `Util`, `Manager`.
- Не смешивай HTTP, domain, MongoDB, gRPC и Kafka в одном классе.
- Не добавляй `@Transactional` механически: текущие Mongo-сценарии его не определяют, а сетевые вызовы внутри транзакции недопустимы.
- Не добавляй JPA-аннотации, Flyway или SQL в MongoDB-проект.

## Domain и ошибки

Создавай сущность через value objects, чтобы инварианты проверялись в одном месте. Например, UUID проверяет `FragranceID`, а диапазон рейтинга -- `FragranceRating`; контроллер не должен дублировать эти правила.

```java
FragranceID id = FragranceID.of(fragranceId);
return fragranceRepository.findById(id)
        .orElseThrow(() -> new IllegalStateException("Fragrance was not found"));
```

Не расширяй текущее использование `NullPointerException` для отсутствующей сущности. В связанной задаче вводи предметное исключение и централизованное безопасное HTTP/gRPC mapping. Не возвращай stack trace, детали MongoDB или gRPC status без преобразования.

## Конвертеры и коллекции

- Конвертер на границе имеет одно направление и ясное имя: `requestToModel`, `resultToResponse`, `entityToModel`, `modelToEntity`, `protoToEntity`.
- HTTP DTO, Mongo DB model и generated Protobuf type не покидают свой адаптер.
- При mapping-е внешнего Proto проверяй отсутствие значений, пустые коллекции и `UNRECOGNIZED` enum.
- Не изменяй полученные collection/DTO in place; возвращай новые значения.

## Spring и наблюдаемость

Use case реализуй как `@Service`, adapter как `@Component`/`@Service` согласно соседнему коду, конфигурацию -- через `@Configuration`. Свойства внешнего соединения помещай в `infra` и оформляй типизированными properties, а не россыпью `@Value`.

Логируй через `infra.logger.Logger` в use case и безопасные ID/тип события в адаптерах. Не логируй email, телефон, request body, protobuf bytes, Mongo URI, токены или исключения с чувствительными данными.

```java
logger.info("Парфюм обновлён с ID {}", fragrance.getId());
```

Перед завершением проверь импорты, компиляцию и тесты затронутого Gradle-модуля.
