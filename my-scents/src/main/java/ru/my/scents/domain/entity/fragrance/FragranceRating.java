package ru.my.scents.domain.entity.fragrance;

import java.util.Objects;

public class FragranceRating {

    private final byte value;

    private FragranceRating(byte value) {
        this.value = value;
    }

    public static FragranceRating of(Byte id) {
        if (id == null) {
            throw new FragranceRatingValidationException("Rating не может быть пустым!");
        }

        if (id < 0 || id > 10) {
            throw new FragranceRatingValidationException("Rating не может быть меньше 0 или больше 10!");
        }

        return new FragranceRating(id);
    }

    public byte getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FragranceRating rating = (FragranceRating) o;
        return Objects.equals(value, rating.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    private static class FragranceRatingValidationException extends RuntimeException {

        private FragranceRatingValidationException(String message) {
            super(message);
        }
    }
}
