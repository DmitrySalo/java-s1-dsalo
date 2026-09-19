# Тестирование LangChain

## Изолированные проверки

Тестируй construction prompts/messages, routing, schema validation, tool policy, обработку ошибок и state transitions изолированно. Для детерминированных unit-тестов применяй fake/stub model и tools; в LangChain Python допустим `GenericFakeChatModel` с заданной последовательностью сообщений и tool calls.

| Изменение | Минимальная проверка |
| --- | --- |
| Prompt/message construction | Unit-тест структуры messages и trusted/untrusted разделения |
| Tool schema или policy | Unit-тест валидации, запрета неразрешённого вызова и результата tool |
| Structured output | Тест валидного и невалидного schema response |
| Routing, retry, timeout | Deterministic test с fake model/tool и ограничением числа вызовов |
| Retrieval/memory | Fake retriever/store и тест scope, TTL или фильтра доступа |
| Исправление дефекта | Регрессионный тест прежнего сценария без live model |

Не используй настоящие API keys, live model, сеть, production vector store или недетерминированный ответ в unit-тестах. Integration-тесты с live provider допускаются только при явной необходимости, должны быть изолированы отдельным marker-ом (например, `integration`) и не запускаться в обычном наборе без настроенных секретов.

## Проверка качества

Запускай Python formatter, linter, type checker и тестовый runner согласно проекту. Проверяй, что изменения не требуют внешней сети для обычного test suite, а fixtures не содержат секреты или персональные данные.
