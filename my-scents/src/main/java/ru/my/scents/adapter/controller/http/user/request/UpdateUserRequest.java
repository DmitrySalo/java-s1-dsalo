package ru.my.scents.adapter.controller.http.user.request;

import lombok.Builder;

@Builder
public record UpdateUserRequest(String id,
                                String firstName,
                                String lastName,
                                String email,
                                String phoneNumber) {
}
