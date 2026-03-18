package ru.my.scents.adapter.controller.http.user.response;

import lombok.Builder;

@Builder
public record AlertResponse(String httpCode,
                            String path,
                            String msg,
                            long timestamp) {
}