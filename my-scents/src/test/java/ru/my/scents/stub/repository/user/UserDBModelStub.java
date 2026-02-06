package ru.my.scents.stub.repository.user;

import java.time.Instant;
import java.util.UUID;
import lombok.experimental.UtilityClass;
import ru.my.scents.adapter.repository.user.model.UserDBModel;

@UtilityClass
public class UserDBModelStub {

    public static UserDBModel createUserDBModel() {
        return UserDBModel.builder()
                .id(UUID.randomUUID().toString())
                .firstName("FirstName")
                .lastName("LastName")
                .email("email@email.com")
                .phoneNumber("88001112222")
                .createdAt(Instant.parse("2025-10-25T11:00:00Z"))
                .updatedAt(Instant.parse("2025-10-25T12:00:00Z"))
                .build();
    }

    public static UserDBModel createUserDBModelWithId(String id) {
        UserDBModel model = createUserDBModel();
        model.setId(id);
        return model;
    }
}