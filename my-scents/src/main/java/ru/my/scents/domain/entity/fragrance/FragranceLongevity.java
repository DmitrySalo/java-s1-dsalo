package ru.my.scents.domain.entity.fragrance;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Стойкость парфюма.
 */
@Getter
@RequiredArgsConstructor
public enum FragranceLongevity {

    VERY_WEAK("Очень слабая"),
    WEAK("Слабая"),
    MIDDLE("Средняя"),
    STRONG("Сильная"),
    ETERNAL("Вечная");

    private final String description;
}
