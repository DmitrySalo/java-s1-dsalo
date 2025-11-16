package ru.my.scents.adapter.controller.http.user.response;

public record UpdateUserResponse(String id,
                                 String firstName,
                                 String lastName,
                                 String email,
                                 String phoneNumber) {
}
