# Ароматека (My Scents)

«Ароматека» -- персональная картотека парфюмов. На текущей стадии это серверное приложение модульного монолита: оно хранит пользователей и парфюмы, предоставляет REST и gRPC-интерфейсы, получает недостающие данные о парфюме по gRPC и обменивается событиями о парфюмах через Kafka.

Проект находится в разработке. Описание основано на исходном коде и конфигурации на 2026-09-18. Полная информация о границах, потоках данных, технологиях и ограничениях приведена в [ARCHITECTURE.md](ARCHITECTURE.md).

## Структура репозитория

```text
.
├── my-scents/                 # Приложение Spring Boot
│   ├── src/main/java/          # Домен, порты, адаптеры и инфраструктура
│   ├── src/main/proto/         # Protobuf-контракты gRPC и Kafka
│   ├── src/test/               # Тесты и тестовая конфигурация
│   └── Dockerfile              # Сборка и запуск контейнера приложения
├── scripts/                    # Скрипты Kafka и Schema Registry
├── docker-compose.yaml         # Локальная инфраструктура
├── ARCHITECTURE.md             # Архитектурная документация
├── REPORT.md                   # Журнал работ
└── promts/                     # Сохранённые задания
```

## Технологии

Модуль `my-scents` собирается Gradle Wrapper 9.1.0 с Java 25 и Spring Boot 3.5.6. Данные хранятся в MongoDB; для интеграций используются gRPC, Protocol Buffers и Apache Kafka. Локальная инфраструктура определена в `docker-compose.yaml`.

Все явно зафиксированные версии зависимостей, плагинов и образов перечислены в [ARCHITECTURE.md](ARCHITECTURE.md#технологии-и-версии).

## Предварительные требования

- JDK 25.
- Docker Engine и Docker Compose для локальных MongoDB, Kafka, Schema Registry и GripMock.
- На Windows используйте `gradlew.bat`, на Linux/macOS -- `./gradlew`.

Не используйте значения подключения и учётные данные из `application.yml` вне локального окружения. Для иных сред передавайте настройки через безопасный внешний механизм конфигурации, например переменные окружения Spring Boot.

## Сборка и тесты

Команды выполняются из корня репозитория:

```shell
# Windows PowerShell
./my-scents/gradlew.bat :my-scents:build

# Linux/macOS
./my-scents/gradlew :my-scents:build
```

Запуск только тестов:

```shell
./my-scents/gradlew.bat :my-scents:test
```

Имеющиеся тесты проверяют MongoDB-репозитории и конвертер gRPC/Protobuf. Интеграционные HTTP-, Kafka- и end-to-end-тесты пока не реализованы.

## Локальный запуск

1. Поднимите зависимости:

```shell
docker compose up -d mongodb kafka schema-registry schema-registry-init fragrance-gripmock kafka-ui
```

2. Запустите приложение:

```shell
./my-scents/gradlew.bat :my-scents:bootRun
```

3. При запуске из Gradle приложение слушает REST-порт `8087`, management-порт `8086` и входящий gRPC-порт `50053`. Внешний gRPC-сервис по умолчанию ожидается на `localhost:50051`.

Actuator: `http://localhost:8086/health`, `http://localhost:8086/metrics`, `http://localhost:8086/prometheus`.

В `docker-compose.yaml` и `my-scents/Dockerfile` есть расхождения с `application.yml` по HTTP/management-портам и health check. Поэтому контейнерный запуск `my-scents-app` требует отдельной проверки и согласования конфигурации перед использованием.

## API

REST-контроллеры реализуют CRUD без path variables; идентификатор передаётся в query parameter.

| Ресурс | Методы и путь |
| --- | --- |
| Пользователи | `POST`, `PUT`, `GET`, `DELETE /api/v1/users` |
| Парфюмы | `POST`, `PUT`, `GET`, `DELETE /api/v1/fragrances` |

Для чтения и удаления обязательны соответственно `?userId=<uuid-v4>` и `?fragranceId=<uuid-v4>`. `POST`, `PUT` и удаление возвращают `200 OK`; у удаления нет тела ответа.

Пример создания парфюма:

```json
{
  "name": "Example",
  "rating": 8,
  "resume": "Example description",
  "concentration": "EAU_DE_PARFUM",
  "type": ["WOODY"],
  "gender": "UNISEX",
  "season": ["FALL", "WINTER"],
  "longevity": "STRONG",
  "sillage": "MODERATE",
  "availability": "AVAILABLE"
}
```

Полный состав REST DTO расположен в `my-scents/src/main/java/ru/my/scents/adapter/controller/http`. Контракт gRPC -- `my-scents/src/main/proto/fragrance/fragrance_service.proto`; метод `FragranceService.GetFragrance` принимает `fragrance_id` и возвращает `GetFragranceResponse`.

В проекте подключён `spring-boot-starter-security`, но явная конфигурация `SecurityFilterChain`, пользователей и правил доступа отсутствует. До публикации API необходимо определить и проверить аутентификацию, авторизацию владельца данных и защиту management-endpoint-ов.

## Документация

- [ARCHITECTURE.md](ARCHITECTURE.md) -- архитектура, контракты, технологии, развёртывание и ограничения.
- [REPORT.md](REPORT.md) -- журнал документирующих работ.
- [promts/research-task-1.md](promts/research-task-1.md) -- исходное задание исследования.
