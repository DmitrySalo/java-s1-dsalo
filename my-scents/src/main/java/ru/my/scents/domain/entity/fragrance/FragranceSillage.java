package ru.my.scents.domain.entity.fragrance;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Шлейф парфюма.
 */
@Getter
@RequiredArgsConstructor
public enum FragranceSillage {

    INTIMATE("Интимный"),
    MODERATE("Умеренный"),
    STRONG("Сильный"),
    VERY_STRONG("Очень сильный");

    private final String description;
}
