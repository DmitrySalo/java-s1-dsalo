package ru.my.scents.adapter.controller.http.user.request;

public record UpdateUserRequest(String id,
                                String firstName,
                                String lastName,
                                String email,
                                String phoneNumber) {
}
