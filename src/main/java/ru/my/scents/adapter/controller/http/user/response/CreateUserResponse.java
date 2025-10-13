package ru.my.scents.adapter.controller.http.user.response;

public record CreateUserResponse(String id,
                                 String firstName,
                                 String lastName,
                                 String email,
                                 String phoneNumber) {
}
