package ru.my.scents.adapter.controller.http.user.response;

import lombok.Builder;

@Builder
public record GetUserResponse(String id,
                              String firstName,
                              String lastName,
                              String email,
                              String phoneNumber) {
}
