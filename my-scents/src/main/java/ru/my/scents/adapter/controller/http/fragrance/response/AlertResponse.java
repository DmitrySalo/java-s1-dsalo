package ru.my.scents.adapter.controller.http.fragrance.response;

import lombok.Builder;

@Builder
public record AlertResponse(String httpCode,
                            String path,
                            String msg,
                            long timestamp) {
}