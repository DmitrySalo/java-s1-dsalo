package ru.my.scents.domain.entity;

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

    public User(UserID id, UserName firstName, UserName lastName, UserEmail email, UserPhoneNumber phoneNumber,
                Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

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
