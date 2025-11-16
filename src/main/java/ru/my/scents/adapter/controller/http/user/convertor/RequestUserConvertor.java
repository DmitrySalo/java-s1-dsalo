package ru.my.scents.adapter.controller.http.user.convertor;

import java.time.LocalDateTime;
import lombok.experimental.UtilityClass;
import ru.my.scents.adapter.controller.http.user.request.CreateUserRequest;
import ru.my.scents.adapter.controller.http.user.request.UpdateUserRequest;
import ru.my.scents.boundary.model.CreateUserParam;
import ru.my.scents.boundary.model.UpdateUserParam;

@UtilityClass
public class RequestUserConvertor {

    public static CreateUserParam createRequestToModel(CreateUserRequest request) {
        return new CreateUserParam(null, request.firstName(), request.lastName(), request.email(),
                request.phoneNumber(), LocalDateTime.now());
    }

    public static UpdateUserParam updateRequestToModel(UpdateUserRequest request) {
        return new UpdateUserParam(request.id(), request.firstName(), request.lastName(), request.email(),
                request.phoneNumber(), LocalDateTime.now());
    }
}
