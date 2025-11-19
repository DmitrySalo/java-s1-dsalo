package ru.my.scents.domain.entity;

import java.util.Objects;
import java.util.regex.Pattern;
import lombok.Getter;

@Getter
public final class UserPhoneNumber {

    private static final Pattern PHONE_NUMBER_PATTERN = Pattern.compile(
            "^\\d{11}$"
    );

    private final String value;

    private UserPhoneNumber(String value) {
        this.value = value;
    }

    public static UserPhoneNumber of(String phoneNumber) {

        if (phoneNumber == null || phoneNumber.isEmpty()) {
            throw new PhoneNumberValidationException("Номер телефона не может быть пустым!");
        }

        String normalized = phoneNumber.trim();

        if (!PHONE_NUMBER_PATTERN.matcher(normalized).matches()) {
            throw new PhoneNumberValidationException("Номер телефона не соответствует шаблону!");
        }

        return new UserPhoneNumber(normalized);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserPhoneNumber that = (UserPhoneNumber) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    private static class PhoneNumberValidationException extends RuntimeException {

        private PhoneNumberValidationException(String message) {
            super(message);
        }
    }
}
