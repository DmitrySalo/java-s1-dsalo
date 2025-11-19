package ru.my.scents.infra.logger;

public interface Logger {

    void error(Throwable... errors);

    void info(String... messages);
}