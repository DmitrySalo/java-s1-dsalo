package ru.my.scents.adapter.controller.http.user.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CreateUserRequest(@NotBlank String firstName,
                                @NotBlank String lastName,
                                @Email String email,
                                @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$") String phoneNumber) {
}
