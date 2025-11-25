package ru.my.scents.adapter.repository.user.converter;

import lombok.experimental.UtilityClass;
import ru.my.scents.adapter.repository.user.model.UserDBModel;
import ru.my.scents.domain.entity.User;
import ru.my.scents.domain.entity.UserEmail;
import ru.my.scents.domain.entity.UserID;
import ru.my.scents.domain.entity.UserName;
import ru.my.scents.domain.entity.UserPhoneNumber;

@UtilityClass
public class UserConverter {

    public static UserDBModel toDbModel(User user) {
        return UserDBModel.builder()
                .id(user.getId().getValue().toString())
                .firstName(user.getFirstName().getValue())
                .lastName(user.getLastName().getValue())
                .email(user.getEmail().getValue())
                .phoneNumber(user.getPhoneNumber().getValue())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    public static User toEntity(UserDBModel dbModel) {
        return User.builder()
                .id(UserID.of(dbModel.getId()))
                .firstName(UserName.of(dbModel.getFirstName()))
                .lastName(UserName.of(dbModel.getLastName()))
                .email(UserEmail.of(dbModel.getEmail()))
                .phoneNumber(UserPhoneNumber.of(dbModel.getPhoneNumber()))
                .createdAt(dbModel.getCreatedAt())
                .updatedAt(dbModel.getUpdatedAt())
                .build();
    }
}