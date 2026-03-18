package ru.my.scents.domain.entity.fragrance;

import java.util.Objects;
import java.util.regex.Pattern;
import lombok.Getter;

@Getter
public final class FragranceName {

    private static final Pattern NAME_PATTERN = Pattern.compile(
            "^[A-ZА-ЯЁ][a-zа-яё]*(?:\\s+(?:[A-ZА-ЯЁa-zа-яё][a-zа-яё]*|\\d+))*$"
    );

    private final String value;

    private FragranceName(String value) {
        this.value = value;
    }

    public static FragranceName of(String name) {

        if (name == null || name.isEmpty()) {
            throw new FragranceNameValidationException("Имя не может быть пустым!");
        }

        String normalized = name.trim();

        if (!NAME_PATTERN.matcher(normalized).matches()) {
            throw new FragranceNameValidationException("Имя не соответствует шаблону!");
        }

        return new FragranceName(normalized);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        FragranceName name = (FragranceName) o;
        return Objects.equals(value, name.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public String toString() {
        return value;
    }

    private static class FragranceNameValidationException extends RuntimeException {

        private FragranceNameValidationException(String message) {
            super(message);
        }
    }
}
