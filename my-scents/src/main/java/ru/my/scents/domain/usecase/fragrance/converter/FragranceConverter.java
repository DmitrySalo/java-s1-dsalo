package ru.my.scents.domain.usecase.fragrance.converter;

import java.util.Set;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;
import ru.my.scents.boundary.model.fragrance.CreateFragranceParam;
import ru.my.scents.boundary.model.fragrance.UpdateFragranceParam;
import ru.my.scents.domain.entity.fragrance.Fragrance;
import ru.my.scents.domain.entity.fragrance.FragranceAvailabilityStatus;
import ru.my.scents.domain.entity.fragrance.FragranceConcentration;
import ru.my.scents.domain.entity.fragrance.FragranceGender;
import ru.my.scents.domain.entity.fragrance.FragranceID;
import ru.my.scents.domain.entity.fragrance.FragranceLongevity;
import ru.my.scents.domain.entity.fragrance.FragranceName;
import ru.my.scents.domain.entity.fragrance.FragranceRating;
import ru.my.scents.domain.entity.fragrance.FragranceResume;
import ru.my.scents.domain.entity.fragrance.FragranceSeason;
import ru.my.scents.domain.entity.fragrance.FragranceSillage;
import ru.my.scents.domain.entity.fragrance.FragranceType;

@UtilityClass
public class FragranceConverter {

    public static Fragrance toEntity(CreateFragranceParam param) {
        if (param == null) {
            throw new IllegalArgumentException("Параметр обязателен!");
        }

        return Fragrance.builder()
                .id(FragranceID.of(param.id()))
                .name(FragranceName.of(param.name()))
                .rating(FragranceRating.of(param.rating()))
                .resume(FragranceResume.of(param.resume()))
                .concentration(FragranceConcentration.valueOf(param.concentration()))
                .type(toEnumSet(param.type(), FragranceType.class))
                .gender(FragranceGender.valueOf(param.gender()))
                .season(toEnumSet(param.season(), FragranceSeason.class))
                .longevity(FragranceLongevity.valueOf(param.longevity()))
                .sillage(FragranceSillage.valueOf(param.sillage()))
                .availability(FragranceAvailabilityStatus.valueOf(param.availability()))
                .createdAt(param.createdAt())
                .build();
    }

    public static Fragrance toEntity(UpdateFragranceParam param) {
        if (param == null) {
            throw new IllegalArgumentException("Параметр обязателен!");
        }

        return Fragrance.builder()
                .id(FragranceID.of(param.id()))
                .name(FragranceName.of(param.name()))
                .rating(FragranceRating.of(param.rating()))
                .resume(FragranceResume.of(param.resume()))
                .concentration(FragranceConcentration.valueOf(param.concentration()))
                .type(toEnumSet(param.type(), FragranceType.class))
                .gender(FragranceGender.valueOf(param.gender()))
                .season(toEnumSet(param.season(), FragranceSeason.class))
                .longevity(FragranceLongevity.valueOf(param.longevity()))
                .sillage(FragranceSillage.valueOf(param.sillage()))
                .availability(FragranceAvailabilityStatus.valueOf(param.availability()))
                .updatedAt(param.updatedAt())
                .build();
    }

    private static <T extends Enum<T>> Set<T> toEnumSet(Set<String> set, Class<T> clazz) {
        return set.stream()
                .map(str -> Enum.valueOf(clazz, str))
                .collect(Collectors.toSet());
    }
}
