package ru.my.scents.domain.entity.user;

import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

public class UserID {

    private static final Pattern UUID_V4_REGEX = Pattern.compile(
            "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-4[0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$"
    );

    private final String value;

    private UserID(String value) {
        this.value = value;
    }

    public static UserID of(String id) {
        if (id == null || id.isEmpty()) {
            throw new UserIDValidationException("ID не может быть пустым!");
        }

        String normalized = id.trim().toLowerCase();

        if (!UUID_V4_REGEX.matcher(normalized).matches()) {
            throw new UserIDValidationException("ID не соответствует шаблону!");
        }

        return new UserID(id);
    }

    public UUID getValue() {
        return UUID.fromString(value);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        UserID userID = (UserID) o;
        return Objects.equals(value, userID.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public String toString() {
        return value;
    }

    private static class UserIDValidationException extends RuntimeException {

        private UserIDValidationException(String message) {
            super(message);
        }
    }
}
