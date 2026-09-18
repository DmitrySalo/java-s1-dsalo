# Тестирование Java-модуля

## Инструменты и расположение

Тесты находятся в `my-scents/src/test/java`; стек -- JUnit Jupiter, AssertJ, Spring Boot Test, Testcontainers MongoDB, REST Assured, `spring-kafka-test` и `grpc-testing`. Текущие тесты покрывают Mongo repository и Proto converter. HTTP, Kafka и gRPC тестов пока нет: добавляй их вместе с изменением соответствующей границы.

Используй `@DisplayName` на русском языке, Arrange-Act-Assert и названия вида `should<Expected>When<Condition>`. Не используй `Thread.sleep`, реальную сеть, реальные секреты, production-данные или зависимость от порядка запуска.

## Выбор теста

| Изменение | Минимальная проверка |
| --- | --- |
| Value object, enum, converter, use case | Быстрый unit-тест без Spring context |
| Mongo query, DB model, repository converter | Repository integration test с MongoDB Testcontainer |
| REST controller, request validation, error mapping | HTTP integration test с `BaseIntegrationTest` и REST Assured |
| gRPC converter/server/gateway | Converter unit-test и gRPC test/stub без внешнего сервиса |
| Kafka producer/listener/retry/DLT | Kafka integration test c test broker и наблюдаемым результатом |
| Исправление дефекта | Регрессионный тест, который воспроизводит старое поведение |

Не поднимай Spring context для чистой доменной логики. Не мокай value objects и converters; мокай порты (`FragranceRepository`, `FragranceGateway`, `FragranceEventPublisher`) на границе use case.

## Примеры

Unit-тест сценария, где вызов порта является частью контракта:

```java
@Test
@DisplayName("Создание парфюма сохраняет сущность и публикует событие")
void shouldSaveFragranceAndPublishEventWhenCreated() {
    // Arrange
    var params = FragranceStub.createParam();

    // Act
    var fragrance = fragranceUseCase.create(params);

    // Assert
    then(fragranceRepository).should().save(fragrance);
    then(eventPublisher).should().send(any(FragranceEvent.class));
}
```

HTTP-проверка должна проверять наблюдаемый контракт, а не метод converter-а:

```java
given()
        .queryParam("fragranceId", fragranceId)
.when()
        .get("/api/v1/fragrances")
.then()
        .statusCode(200)
        .body("id", equalTo(fragranceId));
```

В асинхронных тестах используй ограниченный polling с понятным условием либо механизм test broker; не заменяй синхронизацию фиксированной задержкой.

## Testcontainers и изоляция

Расширяй существующие `BaseIntegrationTest`, `BaseTest`, `TestConfig` и stubs, если это делает сценарий понятнее. MongoDB container уже поднимается в базовом интеграционном тесте через `DynamicPropertySource`. Не создавай отдельный контейнер на тест без иной конфигурации. Перед каждым тестом очищай только затронутые коллекции предсказуемо; не очищай базу реального окружения.

Для Kafka/gRPC не обращайся к `localhost` инфраструктуре разработчика. Тест должен запускать свой broker/server или использовать локальный stub/fake.

## Команды проверки

Из корня репозитория:

```powershell
./my-scents/gradlew.bat :my-scents:test
./my-scents/gradlew.bat :my-scents:build
```

Сначала запускай самый узкий релевантный тест, затем `:my-scents:test`; `:my-scents:build` запускай при изменении production-кода, Protobuf или конфигурации сборки. В итоговом результате перечисляй точные команды, итог и невыполненные проверки с причиной.
