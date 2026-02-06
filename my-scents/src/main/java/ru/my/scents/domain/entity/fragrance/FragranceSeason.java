package ru.my.scents.domain.entity.fragrance;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Наиболее подходящее время.
 */
@Getter
@RequiredArgsConstructor
public enum FragranceSeason {

    SPRING("Весна"),
    SUMMER("Лето"),
    FALL("Осень"),
    WINTER("Зима"),
    DAY("День"),
    NIGHT("Ночь");

    private final String description;
}
