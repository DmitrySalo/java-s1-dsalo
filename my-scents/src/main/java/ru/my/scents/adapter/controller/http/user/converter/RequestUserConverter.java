package ru.my.scents.adapter.controller.http.user.converter;

import java.time.Instant;
import java.util.UUID;
import lombok.experimental.UtilityClass;
import ru.my.scents.adapter.controller.http.user.request.CreateUserRequest;
import ru.my.scents.adapter.controller.http.user.request.UpdateUserRequest;
import ru.my.scents.boundary.model.user.CreateUserParam;
import ru.my.scents.boundary.model.user.UpdateUserParam;

@UtilityClass
public class RequestUserConverter {

    public static CreateUserParam createRequestToModel(CreateUserRequest request) {
        return CreateUserParam.builder()
                .id(UUID.randomUUID().toString())
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .phoneNumber(request.phoneNumber())
                .createdAt(Instant.now())
                .build();
    }

    public static UpdateUserParam updateRequestToModel(UpdateUserRequest request) {
        return UpdateUserParam.builder()
                .id(request.id())
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .phoneNumber(request.phoneNumber())
                .updatedAt(Instant.now())
                .build();
    }
}
