package ru.my.scents.infra.logger;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum LogKeys {

    APP_NAME("appName"),
    REQUEST_ID("requestId");

    @Getter
    private final String value;
}
