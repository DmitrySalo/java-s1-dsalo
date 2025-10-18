package ru.my.scents.adapter.controller.http.user.convertor;

import java.time.LocalDateTime;
import lombok.experimental.UtilityClass;
import ru.my.scents.adapter.controller.http.user.request.CreateUserRequest;
import ru.my.scents.boundary.model.CreateUserParam;

@UtilityClass
public class RequestUserConvertor {

    public static CreateUserParam createRequestToModel(CreateUserRequest request) {
        return new CreateUserParam(null, request.firstName(), request.lastName(), request.email(), request.phoneNumber(), LocalDateTime.now());
    }
}
