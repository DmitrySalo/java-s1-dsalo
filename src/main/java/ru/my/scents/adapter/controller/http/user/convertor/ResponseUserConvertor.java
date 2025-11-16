package ru.my.scents.adapter.controller.http.user.convertor;

import lombok.experimental.UtilityClass;
import ru.my.scents.adapter.controller.http.user.response.CreateUserResponse;
import ru.my.scents.adapter.controller.http.user.response.GetUserResponse;
import ru.my.scents.adapter.controller.http.user.response.UpdateUserResponse;
import ru.my.scents.domain.entity.User;

@UtilityClass
public class ResponseUserConvertor {

    public static CreateUserResponse createResultToResponse(User result) {
        return new CreateUserResponse(result.getId().getValue(), result.getFirstName().getValue(),
                result.getLastName().getValue(), result.getEmail().getValue(), result.getPhoneNumber().getValue());
    }

    public static UpdateUserResponse updateResultToResponse(User result) {
        return new UpdateUserResponse(result.getId().getValue(), result.getFirstName().getValue(),
                result.getLastName().getValue(), result.getEmail().getValue(), result.getPhoneNumber().getValue());
    }

    public static GetUserResponse getResultToResponse(User result) {
        return new GetUserResponse(result.getId().getValue(), result.getFirstName().getValue(),
                result.getLastName().getValue(), result.getEmail().getValue(), result.getPhoneNumber().getValue());
    }
}