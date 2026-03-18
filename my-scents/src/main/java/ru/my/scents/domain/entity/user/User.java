package ru.my.scents.domain.entity.user;

import java.time.Instant;
import java.util.Objects;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class User {

    private final UserID id;
    private final UserName firstName;
    private final UserName lastName;
    private final UserEmail email;
    private final UserPhoneNumber phoneNumber;
    private final Instant createdAt;
    private final Instant updatedAt;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
