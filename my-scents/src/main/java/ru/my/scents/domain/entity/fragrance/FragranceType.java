package ru.my.scents.domain.entity.fragrance;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Тип парфюма.
 */
@Getter
@RequiredArgsConstructor
public enum FragranceType {

    ORIENTAL("Восточный"),
    FLORAL("Цветочный"),
    CHYPRE("Шипровый"),
    FOUGERE("Фужерный"),
    WOODY("Древесный"),
    AQUATIC("Акватический"),
    FRUITY("Фруктовый"),
    CITRUS("Цитрусовый"),
    VANILLA("Ванильный"),
    AMBER("Амбровый"),
    LEATHER("Кожаный"),
    ANIMALIC("Животный"),
    SMOKY("Дымный"),
    BALSAMIC("Бальзамический"),
    MUSKY("Мускусный"),
    SPICY("Пряный"),
    ALDEHYDE("Альдегидный");

    private final String description;
}
