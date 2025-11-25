package ru.my.scents.stub.entity;

import java.time.Instant;
import java.util.UUID;
import lombok.experimental.UtilityClass;
import ru.my.scents.domain.entity.User;
import ru.my.scents.domain.entity.UserEmail;
import ru.my.scents.domain.entity.UserID;
import ru.my.scents.domain.entity.UserName;
import ru.my.scents.domain.entity.UserPhoneNumber;

@UtilityClass
public class UserStub {

    public static User createValidUser() {
        return User.builder()
                .id(UserID.of(UUID.randomUUID().toString()))
                .firstName(UserName.of("Dmitry"))
                .lastName(UserName.of("Salo"))
                .email(UserEmail.of("dmitrysalo@mail.ru"))
                .phoneNumber(UserPhoneNumber.of("88005553535"))
                .createdAt(Instant.now())
                .build();
    }

    public static User createUserWithId(String userId) {
        return User.builder()
                .id(UserID.of(userId))
                .firstName(UserName.of("Dmitry"))
                .lastName(UserName.of("Salo"))
                .email(UserEmail.of("dmitrysalo@mail.ru"))
                .phoneNumber(UserPhoneNumber.of("88005553535"))
                .createdAt(Instant.now())
                .build();
    }
}
