package ru.my.scents.domain.entity.fragrance;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Концентрация парфюма (эфирных масел в парфюме).
 */
@Getter
@RequiredArgsConstructor
public enum FragranceConcentration {

    EAU_DE_COLOGNE("Одеколон"),
    EAU_DE_TOILETTE("Туалетная вода"),
    EAU_DE_PARFUM("Парфюмерная вода"),
    PARFUM("Парфюм"),
    EXTRACT_DE_PARFUM("Парфюмерный экстракт");

    private final String description;
}
