package ru.my.scents.domain.entity;

import java.util.Objects;
import java.util.regex.Pattern;
import lombok.Getter;

@Getter
public final class UserEmail {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    );

    private final String value;

    private UserEmail(String value) {
        this.value = value;
    }

    public static UserEmail of(String email) {

        if (email == null || email.isEmpty()) {
            throw new EmailValidationException("Email не может быть пустым!");
        }

        String normalized = email.trim().toLowerCase();

        if (!EMAIL_PATTERN.matcher(normalized).matches()) {
            throw new EmailValidationException("Email не соответствует шаблону!");
        }

        if (normalized.length() > 254) {
            throw new EmailValidationException("Email превышает длину в 254 символа!");
        }

        return new UserEmail(normalized);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserEmail userEmail = (UserEmail) o;
        return Objects.equals(value, userEmail.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    private static class EmailValidationException extends RuntimeException {

        private EmailValidationException(String message) {
            super(message);
        }
    }
}
