package ru.my.scents.domain.entity;

import java.time.LocalDateTime;
import java.util.Objects;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class User {

    private UserID id;
    private UserName firstName;
    private UserName lastName;
    private UserEmail email;
    private UserPhoneNumber phoneNumber;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public User(UserID id, UserName firstName, UserName lastName, UserEmail email, UserPhoneNumber phoneNumber,
                LocalDateTime createdAt, LocalDateTime updatedAt) {
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
