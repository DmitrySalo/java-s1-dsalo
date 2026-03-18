package ru.my.scents.domain.usecase.user.converter;

import lombok.experimental.UtilityClass;
import ru.my.scents.boundary.model.user.CreateUserParam;
import ru.my.scents.boundary.model.user.UpdateUserParam;
import ru.my.scents.domain.entity.user.User;
import ru.my.scents.domain.entity.user.UserEmail;
import ru.my.scents.domain.entity.user.UserID;
import ru.my.scents.domain.entity.user.UserName;
import ru.my.scents.domain.entity.user.UserPhoneNumber;

@UtilityClass
public class UserConverter {

    public static User toEntity(CreateUserParam param) {
        if (param == null) {
            throw new IllegalArgumentException("Параметр обязателен!");
        }

        return User.builder()
                .id(UserID.of(param.id()))
                .firstName(UserName.of(param.firstName()))
                .lastName(UserName.of(param.lastName()))
                .phoneNumber(UserPhoneNumber.of(param.phoneNumber()))
                .email(UserEmail.of(param.email()))
                .createdAt(param.createdAt())
                .build();
    }

    public static User toEntity(UpdateUserParam param) {
        if (param == null) {
            throw new IllegalArgumentException("Параметр обязателен!");
        }

        return User.builder()
                .id(UserID.of(param.id()))
                .firstName(UserName.of(param.firstName()))
                .lastName(UserName.of(param.lastName()))
                .phoneNumber(UserPhoneNumber.of(param.phoneNumber()))
                .email(UserEmail.of(param.email()))
                .updatedAt(param.updatedAt())
                .build();
    }
}
