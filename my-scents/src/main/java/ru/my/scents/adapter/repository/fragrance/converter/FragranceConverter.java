package ru.my.scents.adapter.repository.fragrance.converter;

import java.util.Set;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;
import ru.my.scents.adapter.repository.fragrance.model.FragranceDBModel;
import ru.my.scents.domain.entity.fragrance.Fragrance;
import ru.my.scents.domain.entity.fragrance.FragranceAvailabilityStatus;
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

    public static FragranceDBModel toDbModel(Fragrance fragrance) {
        if (fragrance == null) {
            throw new IllegalArgumentException("Парфюм обязателен!");
        }
        return FragranceDBModel.builder()
                .id(fragrance.getId().getValue().toString())
                .name(fragrance.getName().getValue())
                .rating(fragrance.getRating().getValue())
                .resume(fragrance.getResume().getValue())
                .type(toStringSet(fragrance.getType()))
                .season(toStringSet(fragrance.getSeason()))
                .sillage(fragrance.getSillage().name())
                .availability(fragrance.getAvailability().name())
                .gender(fragrance.getGender().name())
                .longevity(fragrance.getLongevity().name())
                .createdAt(fragrance.getCreatedAt())
                .updatedAt(fragrance.getUpdatedAt())
                .build();
    }

    public static Fragrance toEntity(FragranceDBModel dbModel) {
        if (dbModel == null) {
            throw new IllegalArgumentException("Парфюм обязателен!");
        }
        return Fragrance.builder()
                .id(FragranceID.of(dbModel.getId()))
                .name(FragranceName.of(dbModel.getName()))
                .rating(FragranceRating.of(dbModel.getRating()))
                .resume(FragranceResume.of(dbModel.getResume()))
                .type(toEnumSet(dbModel.getType(), FragranceType.class))
                .season(toEnumSet(dbModel.getSeason(), FragranceSeason.class))
                .sillage(FragranceSillage.valueOf(dbModel.getSillage()))
                .availability(FragranceAvailabilityStatus.valueOf(dbModel.getAvailability()))
                .gender(FragranceGender.valueOf(dbModel.getGender()))
                .longevity(FragranceLongevity.valueOf(dbModel.getLongevity()))
                .createdAt(dbModel.getCreatedAt())
                .updatedAt(dbModel.getUpdatedAt())
                .build();
    }

    private static Set<String> toStringSet(Set<? extends Enum<?>> enumSet) {
        return enumSet.stream()
                .map(Enum::name)
                .collect(Collectors.toSet());
    }

    private static <T extends Enum<T>> Set<T> toEnumSet(Set<String> enumSet, Class<T> clazz) {
        return enumSet.stream()
                .map(str -> Enum.valueOf(clazz, str))
                .collect(Collectors.toSet());
    }
}