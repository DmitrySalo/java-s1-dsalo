package ru.my.scents.domain.entity.fragrance;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Условная ориентация парфюма.
 */
@Getter
@RequiredArgsConstructor
public enum FragranceGender {

    MALE("Мужской"),
    FEMALE("Женский"),
    UNISEX("Унисекс");

    private final String description;
}
