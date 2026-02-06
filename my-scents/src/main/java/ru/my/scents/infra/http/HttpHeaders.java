package ru.my.scents.infra.http;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum HttpHeaders {

    REQUEST_ID_HEADER("X-Request-ID");

    @Getter
    private final String value;
}
