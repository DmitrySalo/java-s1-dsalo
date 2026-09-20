# Задание: Windows Job containment для проверки проекта

Для приёмки нужно:

1. Создавать OS-level containment до начала исполнения процесса, например Windows Job Object с запуском процесса в suspended state, назначением Job и последующим resume.
2. Закрывать или уничтожать Job Object во всех аварийных сценариях: timeout, OUTPUT_LIMIT_EXCEEDED, interruption, ошибка reader.
3. Добавить реальный Windows integration test: helper process создаёт долгоживущего child process, после чего test подтверждает завершение обоих PID.
4. Работу продолжи в текущей ветке.
