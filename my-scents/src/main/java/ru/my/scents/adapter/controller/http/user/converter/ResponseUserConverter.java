package ru.my.scents.adapter.controller.http.user.converter;

import lombok.experimental.UtilityClass;
import ru.my.scents.adapter.controller.http.user.response.CreateUserResponse;
import ru.my.scents.adapter.controller.http.user.response.GetUserResponse;
import ru.my.scents.adapter.controller.http.user.response.UpdateUserResponse;
import ru.my.scents.domain.entity.user.User;

@UtilityClass
public class ResponseUserConverter {

    public static CreateUserResponse createResultToResponse(User result) {
        if (result == null) {
            throw new IllegalArgumentException("Пользователь обязателен!");
        }

        return CreateUserResponse.builder()
                .id(result.getId().getValue().toString())
                .firstName(result.getFirstName().getValue())
                .lastName(result.getLastName().getValue())
                .email(result.getEmail().getValue())
                .phoneNumber(result.getPhoneNumber().getValue())
                .build();
    }

    public static UpdateUserResponse updateResultToResponse(User result) {
        if (result == null) {
            throw new IllegalArgumentException("Пользователь обязателен!");
        }

        return UpdateUserResponse.builder()
                .id(result.getId().getValue().toString())
                .firstName(result.getFirstName().getValue())
                .lastName(result.getLastName().getValue())
                .email(result.getEmail().getValue())
                .phoneNumber(result.getPhoneNumber().getValue())
                .build();
    }

    public static GetUserResponse getResultToResponse(User result) {
        if (result == null) {
            throw new IllegalArgumentException("Пользователь обязателен!");
        }

        return GetUserResponse.builder()
                .id(result.getId().getValue().toString())
                .firstName(result.getFirstName().getValue())
                .lastName(result.getLastName().getValue())
                .email(result.getEmail().getValue())
                .phoneNumber(result.getPhoneNumber().getValue())
                .build();
    }
}