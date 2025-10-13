package ru.my.scents.adapter.controller.http.user.convertor;

import lombok.experimental.UtilityClass;
import ru.my.scents.adapter.controller.http.user.response.CreateUserResponse;
import ru.my.scents.boundary.model.User;

@UtilityClass
public class ResponseUserConvertor {

    public static CreateUserResponse createResultToResponse(User result) {
        return new CreateUserResponse(result.getId(), result.getFirstName(), result.getLastName(), result.getEmail(), result.getPhoneNumber());
    }
}