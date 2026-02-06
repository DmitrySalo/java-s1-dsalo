package ru.my.scents.stub.entity;

import java.time.Instant;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;
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
public class FragranceStub {

    public static Fragrance createValidFragrance() {
        return Fragrance.builder()
                .id(FragranceID.of(UUID.randomUUID().toString()))
                .name(FragranceName.of("Fahrenheit"))
                .resume(FragranceResume.of("Прекрасно!"))
                .type(Set.of(FragranceType.FOUGERE))
                .gender(FragranceGender.MALE)
                .season(Set.of(FragranceSeason.DAY, FragranceSeason.NIGHT, FragranceSeason.WINTER,
                        FragranceSeason.SPRING, FragranceSeason.SUMMER, FragranceSeason.FALL))
                .sillage(FragranceSillage.STRONG)
                .longevity(FragranceLongevity.STRONG)
                .availability(FragranceAvailabilityStatus.AVAILABLE)
                .rating(FragranceRating.of((byte) 8))
                .createdAt(Instant.now())
                .build();
    }

    public static Fragrance createFragranceWithId(String fragranceId) {
        return Fragrance.builder()
                .id(FragranceID.of(fragranceId))
                .name(FragranceName.of("Fahrenheit"))
                .resume(FragranceResume.of("Прекрасно!"))
                .type(Set.of(FragranceType.FOUGERE))
                .gender(FragranceGender.MALE)
                .season(Set.of(FragranceSeason.DAY, FragranceSeason.NIGHT, FragranceSeason.WINTER,
                        FragranceSeason.SPRING, FragranceSeason.SUMMER, FragranceSeason.FALL))
                .sillage(FragranceSillage.STRONG)
                .longevity(FragranceLongevity.STRONG)
                .availability(FragranceAvailabilityStatus.AVAILABLE)
                .rating(FragranceRating.of((byte) 8))
                .createdAt(Instant.now())
                .build();
    }

    public static Fragrance createFragranceWithUpdatedAt() {
        return Fragrance.builder()
                .id(FragranceID.of(UUID.randomUUID().toString()))
                .name(FragranceName.of("Fahrenheit"))
                .resume(FragranceResume.of("Прекрасно!"))
                .type(Set.of(FragranceType.FOUGERE))
                .gender(FragranceGender.MALE)
                .season(Set.of(FragranceSeason.DAY, FragranceSeason.NIGHT))
                .sillage(FragranceSillage.STRONG)
                .longevity(FragranceLongevity.STRONG)
                .availability(FragranceAvailabilityStatus.AVAILABLE)
                .rating(FragranceRating.of((byte) 8))
                .createdAt(Instant.now().minusSeconds(3600))
                .updatedAt(Instant.now())
                .build();
    }

    public static Fragrance createFragranceWithAllTypes() {
        return Fragrance.builder()
                .id(FragranceID.of(UUID.randomUUID().toString()))
                .name(FragranceName.of("All Types Fragrance"))
                .resume(FragranceResume.of("Тестовый аромат со всеми типами"))
                .type(Arrays.stream(FragranceType.values()).collect(Collectors.toSet()))
                .gender(FragranceGender.UNISEX)
                .season(Set.of(FragranceSeason.SPRING))
                .sillage(FragranceSillage.MODERATE)
                .longevity(FragranceLongevity.MIDDLE)
                .availability(FragranceAvailabilityStatus.AVAILABLE)
                .rating(FragranceRating.of((byte) 7))
                .createdAt(Instant.now())
                .build();
    }

    public static Fragrance createFragranceWithAllSeasons() {
        return Fragrance.builder()
                .id(FragranceID.of(UUID.randomUUID().toString()))
                .name(FragranceName.of("All Seasons Fragrance"))
                .resume(FragranceResume.of("Тестовый аромат со всеми сезонами"))
                .type(Set.of(FragranceType.WOODY))
                .gender(FragranceGender.UNISEX)
                .season(Arrays.stream(FragranceSeason.values()).collect(Collectors.toSet()))
                .sillage(FragranceSillage.MODERATE)
                .longevity(FragranceLongevity.MIDDLE)
                .availability(FragranceAvailabilityStatus.AVAILABLE)
                .rating(FragranceRating.of((byte) 7))
                .createdAt(Instant.now())
                .build();
    }

    public static Fragrance createFragranceWithGender(FragranceGender gender) {
        return Fragrance.builder()
                .id(FragranceID.of(UUID.randomUUID().toString()))
                .name(FragranceName.of("Gender Test Fragrance"))
                .resume(FragranceResume.of("Тестовый аромат"))
                .type(Set.of(FragranceType.FLORAL))
                .gender(gender)
                .season(Set.of(FragranceSeason.SPRING))
                .sillage(FragranceSillage.MODERATE)
                .longevity(FragranceLongevity.MIDDLE)
                .availability(FragranceAvailabilityStatus.AVAILABLE)
                .rating(FragranceRating.of((byte) 6))
                .createdAt(Instant.now())
                .build();
    }

    public static Fragrance createFragranceWithLongevity(FragranceLongevity longevity) {
        return Fragrance.builder()
                .id(FragranceID.of(UUID.randomUUID().toString()))
                .name(FragranceName.of("Longevity Test Fragrance"))
                .resume(FragranceResume.of("Тестовый аромат"))
                .type(Set.of(FragranceType.ORIENTAL))
                .gender(FragranceGender.MALE)
                .season(Set.of(FragranceSeason.WINTER))
                .sillage(FragranceSillage.STRONG)
                .longevity(longevity)
                .availability(FragranceAvailabilityStatus.AVAILABLE)
                .rating(FragranceRating.of((byte) 8))
                .createdAt(Instant.now())
                .build();
    }

    public static Fragrance createFragranceWithSillage(FragranceSillage sillage) {
        return Fragrance.builder()
                .id(FragranceID.of(UUID.randomUUID().toString()))
                .name(FragranceName.of("Sillage Test Fragrance"))
                .resume(FragranceResume.of("Тестовый аромат"))
                .type(Set.of(FragranceType.AQUATIC))
                .gender(FragranceGender.FEMALE)
                .season(Set.of(FragranceSeason.SUMMER))
                .sillage(sillage)
                .longevity(FragranceLongevity.MIDDLE)
                .availability(FragranceAvailabilityStatus.AVAILABLE)
                .rating(FragranceRating.of((byte) 7))
                .createdAt(Instant.now())
                .build();
    }

    public static Fragrance createFragranceWithAvailability(FragranceAvailabilityStatus availability) {
        return Fragrance.builder()
                .id(FragranceID.of(UUID.randomUUID().toString()))
                .name(FragranceName.of("Availability Test Fragrance"))
                .resume(FragranceResume.of("Тестовый аромат"))
                .type(Set.of(FragranceType.CITRUS))
                .gender(FragranceGender.UNISEX)
                .season(Set.of(FragranceSeason.DAY))
                .sillage(FragranceSillage.INTIMATE)
                .longevity(FragranceLongevity.WEAK)
                .availability(availability)
                .rating(FragranceRating.of((byte) 5))
                .createdAt(Instant.now())
                .build();
    }

    public static Fragrance createFragranceWithEmptyTypes() {
        return Fragrance.builder()
                .id(FragranceID.of(UUID.randomUUID().toString()))
                .name(FragranceName.of("Empty Types Fragrance"))
                .resume(FragranceResume.of("Тестовый аромат без типов"))
                .type(Set.of())
                .gender(FragranceGender.MALE)
                .season(Set.of(FragranceSeason.SPRING))
                .sillage(FragranceSillage.MODERATE)
                .longevity(FragranceLongevity.MIDDLE)
                .availability(FragranceAvailabilityStatus.AVAILABLE)
                .rating(FragranceRating.of((byte) 6))
                .createdAt(Instant.now())
                .build();
    }

    public static Fragrance createFragranceWithEmptySeasons() {
        return Fragrance.builder()
                .id(FragranceID.of(UUID.randomUUID().toString()))
                .name(FragranceName.of("Empty Seasons Fragrance"))
                .resume(FragranceResume.of("Тестовый аромат без сезонов"))
                .type(Set.of(FragranceType.WOODY))
                .gender(FragranceGender.MALE)
                .season(Set.of())
                .sillage(FragranceSillage.MODERATE)
                .longevity(FragranceLongevity.MIDDLE)
                .availability(FragranceAvailabilityStatus.AVAILABLE)
                .rating(FragranceRating.of((byte) 6))
                .createdAt(Instant.now())
                .build();
    }
}
