# API-контракты LangChain

## Границы агента

Контрактом являются входные сообщения, state, tool schemas, structured output, API agent-а и семантика ошибок. До изменения изучи вызывающий код, prompts, tools и схемы. Считай breaking change переименование tool или поля, изменение типа/обязательности schema, новый side effect, изменение формата state или structured response.

- Для tool применяй узкую типизированную входную схему и явную выходную схему. Валидируй аргументы до исполнения, а результат до передачи следующему звену.
- Для результата, потребляемого программой, используй structured output с schema (например, Pydantic model), а не парсинг свободного текста. Обрабатывай validation error как отдельную ошибку сценария.
- Не передавай raw внутренние исключения, трассировки, provider error или tool output напрямую пользователю.
- Новые tools с побочным эффектом должны иметь явную серверную точку подтверждения, а не только prompt-инструкцию.

## Внешние API и webhooks

Применяй правила `.opencode/instructions/python-api-contracts.md` к API вокруг агента. Для model provider задавай timeout и ограниченный retry; не меняй model/provider или параметры вызова как побочный эффект несвязанной задачи.

## Проверка

Добавляй тесты schema compatibility, invalid tool input, structured output validation, failure provider/tool и запрет side effect без подтверждения. Тест проверяет state и наблюдаемый контракт вызывающего кода, а не точную формулировку свободного текста модели.
