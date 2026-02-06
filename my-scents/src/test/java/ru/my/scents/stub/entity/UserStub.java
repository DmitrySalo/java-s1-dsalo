package ru.my.scents.stub.entity;

import java.time.Instant;
import java.util.UUID;
import lombok.experimental.UtilityClass;
import ru.my.scents.domain.entity.user.User;
import ru.my.scents.domain.entity.user.UserEmail;
import ru.my.scents.domain.entity.user.UserID;
import ru.my.scents.domain.entity.user.UserName;
import ru.my.scents.domain.entity.user.UserPhoneNumber;

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
