package ru.my.scents.domain.entity.fragrance;

import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

public class FragranceID {

    private static final Pattern UUID_V4_REGEX = Pattern.compile(
            "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-4[0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$"
    );

    private final String value;

    private FragranceID(String value) {
        this.value = value;
    }

    public static FragranceID of(String id) {
        if (id == null || id.isEmpty()) {
            throw new FragranceIDValidationException("ID не может быть пустым!");
        }

        String normalized = id.trim().toLowerCase();

        if (!UUID_V4_REGEX.matcher(normalized).matches()) {
            throw new FragranceIDValidationException("ID не соответствует шаблону!");
        }

        return new FragranceID(id);
    }

    public UUID getValue() {
        return UUID.fromString(value);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FragranceID userID = (FragranceID) o;
        return Objects.equals(value, userID.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    private static class FragranceIDValidationException extends RuntimeException {

        private FragranceIDValidationException(String message) {
            super(message);
        }
    }
}
