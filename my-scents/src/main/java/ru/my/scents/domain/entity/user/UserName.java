package ru.my.scents.domain.entity.user;

import java.util.Objects;
import java.util.regex.Pattern;
import lombok.Getter;

@Getter
public final class UserName {

    private static final Pattern USER_NAME_PATTERN = Pattern.compile(
            "^[A-ZА-ЯЁ][a-zа-яё]{2,255}$"
    );

    private final String value;

    private UserName(String value) {
        this.value = value;
    }

    public static UserName of(String name) {

        if (name == null || name.isEmpty()) {
            throw new UserNameValidationException("Имя не может быть пустым!");
        }

        String normalized = name.trim();

        if (!USER_NAME_PATTERN.matcher(normalized).matches()) {
            throw new UserNameValidationException("Имя не соответствует шаблону!");
        }

        return new UserName(normalized);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserName userName = (UserName) o;
        return Objects.equals(value, userName.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    private static class UserNameValidationException extends RuntimeException {

        private UserNameValidationException(String message) {
            super(message);
        }
    }
}
