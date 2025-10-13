## 1. Сущность и функционал
   Сущность **User**
   Сущность User представляет пользователя в системе. Она включает следующие поля:
   - **id:** Уникальный идентификатор пользователя (строка, генерируется автоматически как UUID).
   - **firstName:** Имя пользователя (строка, обязательно).
   - **lastName:** Фамилия пользователя (строка, обязательно).
   - **email:** Электронная почта пользователя (строка, обязательно, валидируется как email).
   - **phoneNumber:** Номер телефона пользователя (строка, обязательно).
   - **createdAt:** Дата и время создания пользователя (LocalDateTime, генерируется автоматически).

   Функционал
   - **API предоставляет единственную операцию:** создание нового пользователя via POST-запрос.
   - **Создание пользователя:** Принимает данные в JSON-формате, валидирует их, генерирует ID и дату создания, сохраняет в in-memory Map (HashMap) и возвращает созданного пользователя.
   - **Обработка ошибок:** Глобальный обработчик ошибок (ErrorResolver) перехватывает исключения: IllegalArgumentException (для валидационных ошибок) возвращает HTTP 400 с деталями.
   RuntimeException (для системных ошибок) возвращает HTTP 500 с деталями.

## 2. Используемый стек технологий
   - Язык программирования: Java 25.
   - Фреймворк: Spring Boot 3.5.6 (включая starters для web, data-jpa, validation, security, actuator, oauth2-resource-server, oauth2-client).
   - Сборка и зависимости: Gradle (с плагинами spring-boot, dependency-management). Ключевые библиотеки:Spring Boot Starters: web, web-services, jetty (как сервер вместо Tomcat), security, retry, aspects.
   - База данных: PostgreSQL (runtime), Flyway для миграций (хотя в текущей реализации используется in-memory Map, а не реальная БД).
   - ORM: Spring Data JPA, Hibernate (с jpamodelgen), Blaze Persistence для расширенных запросов.
   - Маппинг: MapStruct 1.6.3 для конвертации объектов.
   - Логирование: Logback.
   - Мониторинг: Micrometer с Prometheus.
   - Другое: Lombok для boilerplate-кода, Jakarta Validation для валидации.
   - Сервер: Jetty.
   - Хранение данных: In-memory.
   - Дополнительно: Поддержка аспектов (AOP), retry-механизмов и OAuth2 (не используются в текущем коде).

## 3. Инструкции по запуску
   **Требования:**
   - Java 25 (или совместимая версия).
   - Gradle (установлен локально или через wrapper).
   - Опционально: PostgreSQL (если планируется переход на реальную БД; в текущей версии не требуется).

**Шаги по запуску:**
1. Клонируйте репозиторий (если применимо) или скопируйте код в проект.
2. Соберите проект:
```shell
   ./gradlew build
```
3. Запустите приложение:
```shell
  ./gradlew bootRun
```

**Доступ к API:** Приложение запускается на порту 8080 по умолчанию. Базовый URL: http://localhost:8080/api/v1/users.

**Мониторинг:** Actuator endpoints доступны по /actuator (например, /actuator/health для проверки статуса).

## 4. Примеры запросов
   API использует JSON для запросов и ответов. Тестируйте с помощью инструментов вроде Postman или curl.
   **Пример успешного создания пользователя (POST /api/v1/users)**
   **Запрос:**
   
```text
POST http://localhost:8080/api/v1/users
Content-Type: application/json

{
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "phoneNumber": "+1234567890"
}
```

**Ответ (HTTP 200):**
```text
{
    "id": "uuid-generated-string",
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "phoneNumber": "+1234567890",
    "createdAt": "2025-10-13T14:45:00"
}
```

**Пример ошибки валидации (некорректный email)**
**Запрос:**
```text
POST http://localhost:8080/api/v1/users
Content-Type: application/json

{
    "firstName": "John",
    "lastName": "Doe",
    "email": "invalid-email",
    "phoneNumber": "+1234567890"
}
```

**Ответ (HTTP 400):**
```text
{
    "httpCode": "BAD_REQUEST",
    "path": "/api/v1/users",
    "errorCode": "dfe99c9e-001",
    "msg": "Invalid email format",
    "timestamp": 1728917100000
}
```

**Пример системной ошибки (если use case выбросит RuntimeException)**
**Ответ (HTTP 500):**
```text
{
    "httpCode": "INTERNAL_SERVER_ERROR",
    "path": "/api/v1/users",
    "errorCode": "dfe99c9e-002",
    "msg": "Unexpected error occurred",
    "timestamp": 1728917100000
}
```