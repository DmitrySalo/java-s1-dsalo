package ru.my.scents.domain.entity.fragrance;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Статус наличия парфюма.
 */
@Getter
@RequiredArgsConstructor
public enum FragranceAvailabilityStatus {

    AVAILABLE("Присутствует", true),
    UNAVAILABLE("Отсутствует", false);

    private final String description;
    private final boolean available;
}