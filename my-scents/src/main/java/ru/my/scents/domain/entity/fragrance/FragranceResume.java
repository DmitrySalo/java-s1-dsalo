package ru.my.scents.domain.entity.fragrance;

import java.util.Objects;
import lombok.Getter;

@Getter
public final class FragranceResume {

    private final String value;

    private FragranceResume(String value) {
        this.value = value;
    }

    public static FragranceResume of(String resume) {

        if (resume == null || resume.isEmpty()) {
            throw new FragranceResumeValidationException("Резюме не может быть пустым!");
        }

        return new FragranceResume(resume);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        FragranceResume resume = (FragranceResume) o;
        return Objects.equals(value, resume.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public String toString() {
        return value;
    }

    private static class FragranceResumeValidationException extends RuntimeException {

        private FragranceResumeValidationException(String message) {
            super(message);
        }
    }
}
