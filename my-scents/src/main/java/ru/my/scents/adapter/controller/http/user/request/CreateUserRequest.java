package ru.my.scents.adapter.controller.http.user.request;

import lombok.Builder;

@Builder
public record CreateUserRequest(String firstName,
                                String lastName,
                                String email,
                                String phoneNumber) {
}
