package ru.my.scents.stub.proto;

import com.google.protobuf.Timestamp;
import java.time.Instant;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import lombok.experimental.UtilityClass;
import ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceAvailabilityStatus;
import ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceData;
import ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceGender;
import ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceLongevity;
import ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceSeason;
import ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceSillage;
import ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceType;

@UtilityClass
public class FragranceDataStub {

    public static FragranceData createValidFragrance() {
        return FragranceData.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setRating(6)
                .setName("Benzin")
                .setResume("Интересный, дымно-кожаный аромат на весну")
                .addAllSeason(Set.of(FragranceSeason.FRAGRANCE_SEASON_SPRING, FragranceSeason.FRAGRANCE_SEASON_FALL,
                        FragranceSeason.FRAGRANCE_SEASON_DAY))
                .setGender(FragranceGender.FRAGRANCE_GENDER_UNISEX)
                .addType(FragranceType.FRAGRANCE_TYPE_LEATHER)
                .setSillage(FragranceSillage.FRAGRANCE_SILLAGE_STRONG)
                .setLongevity(FragranceLongevity.FRAGRANCE_LONGEVITY_STRONG)
                .setAvailability(FragranceAvailabilityStatus.FRAGRANCE_AVAILABILITY_STATUS_AVAILABLE)
                .setCreatedAt(toProtoTimestamp(Instant.parse("2026-01-22T11:00:00Z")))
                .setUpdatedAt(toProtoTimestamp(Instant.parse("2026-01-22T12:00:00Z")))
                .build();
    }

    public static FragranceData createValidFragranceWithId(String id) {
        return FragranceData.newBuilder()
                .setId(id)
                .setRating(6)
                .setName("Benzin")
                .setResume("Интересный, дымно-кожаный аромат на весну")
                .addSeason(FragranceSeason.FRAGRANCE_SEASON_SPRING)
                .addSeason(FragranceSeason.FRAGRANCE_SEASON_FALL)
                .addSeason(FragranceSeason.FRAGRANCE_SEASON_DAY)
                .setGender(FragranceGender.FRAGRANCE_GENDER_UNISEX)
                .addType(FragranceType.FRAGRANCE_TYPE_LEATHER)
                .setSillage(FragranceSillage.FRAGRANCE_SILLAGE_STRONG)
                .setLongevity(FragranceLongevity.FRAGRANCE_LONGEVITY_STRONG)
                .setAvailability(FragranceAvailabilityStatus.FRAGRANCE_AVAILABILITY_STATUS_AVAILABLE)
                .setCreatedAt(toProtoTimestamp(Instant.parse("2026-01-22T11:00:00Z")))
                .setUpdatedAt(toProtoTimestamp(Instant.parse("2026-01-22T12:00:00Z")))
                .build();
    }

    public static FragranceData createFragranceWithType(FragranceType type) {
        return FragranceData.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setRating(6)
                .setName("Benzin")
                .setResume("Интересный, дымно-кожаный аромат на весну")
                .addSeason(FragranceSeason.FRAGRANCE_SEASON_SPRING)
                .addSeason(FragranceSeason.FRAGRANCE_SEASON_FALL)
                .addSeason(FragranceSeason.FRAGRANCE_SEASON_DAY)
                .setGender(FragranceGender.FRAGRANCE_GENDER_UNISEX)
                .addType(type)
                .setSillage(FragranceSillage.FRAGRANCE_SILLAGE_STRONG)
                .setLongevity(FragranceLongevity.FRAGRANCE_LONGEVITY_STRONG)
                .setAvailability(FragranceAvailabilityStatus.FRAGRANCE_AVAILABILITY_STATUS_AVAILABLE)
                .setCreatedAt(toProtoTimestamp(Instant.parse("2026-01-22T11:00:00Z")))
                .setUpdatedAt(toProtoTimestamp(Instant.parse("2026-01-22T12:00:00Z")))
                .build();
    }

    public static FragranceData createFragranceWithGender(FragranceGender gender) {
        return FragranceData.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setRating(6)
                .setName("Gender Test Fragrance")
                .setResume("Тестовый аромат")
                .addSeason(FragranceSeason.FRAGRANCE_SEASON_SPRING)
                .setGender(gender)
                .addType(FragranceType.FRAGRANCE_TYPE_FLORAL)
                .setSillage(FragranceSillage.FRAGRANCE_SILLAGE_MODERATE)
                .setLongevity(FragranceLongevity.FRAGRANCE_LONGEVITY_MIDDLE)
                .setAvailability(FragranceAvailabilityStatus.FRAGRANCE_AVAILABILITY_STATUS_AVAILABLE)
                .setCreatedAt(toProtoTimestamp(Instant.ofEpochSecond(System.currentTimeMillis() / 1000)))
                .build();
    }

    public static FragranceData createFragranceWithSeasons(Collection<FragranceSeason> seasons) {
        return FragranceData.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setRating(6)
                .setName("Seasons Test Fragrance")
                .setResume("Тестовый аромат")
                .addAllSeason(seasons)
                .setGender(FragranceGender.FRAGRANCE_GENDER_UNISEX)
                .addType(FragranceType.FRAGRANCE_TYPE_WOODY)
                .setSillage(FragranceSillage.FRAGRANCE_SILLAGE_MODERATE)
                .setLongevity(FragranceLongevity.FRAGRANCE_LONGEVITY_MIDDLE)
                .setAvailability(FragranceAvailabilityStatus.FRAGRANCE_AVAILABILITY_STATUS_AVAILABLE)
                .setCreatedAt(toProtoTimestamp(Instant.ofEpochSecond(System.currentTimeMillis() / 1000)))
                .build();
    }

    public static FragranceData createFragranceWithLongevity(FragranceLongevity longevity) {
        return FragranceData.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setRating(7)
                .setName("Longevity Test Fragrance")
                .setResume("Тестовый аромат")
                .addSeason(FragranceSeason.FRAGRANCE_SEASON_WINTER)
                .setGender(FragranceGender.FRAGRANCE_GENDER_MALE)
                .addType(FragranceType.FRAGRANCE_TYPE_ORIENTAL)
                .setSillage(FragranceSillage.FRAGRANCE_SILLAGE_STRONG)
                .setLongevity(longevity)
                .setAvailability(FragranceAvailabilityStatus.FRAGRANCE_AVAILABILITY_STATUS_AVAILABLE)
                .setCreatedAt(toProtoTimestamp(Instant.ofEpochSecond(System.currentTimeMillis() / 1000)))
                .build();
    }

    public static FragranceData createFragranceWithSillage(FragranceSillage sillage) {
        return FragranceData.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setRating(7)
                .setName("Sillage Test Fragrance")
                .setResume("Тестовый аромат")
                .addSeason(FragranceSeason.FRAGRANCE_SEASON_SUMMER)
                .setGender(FragranceGender.FRAGRANCE_GENDER_FEMALE)
                .addType(FragranceType.FRAGRANCE_TYPE_AQUATIC)
                .setSillage(sillage)
                .setLongevity(FragranceLongevity.FRAGRANCE_LONGEVITY_MIDDLE)
                .setAvailability(FragranceAvailabilityStatus.FRAGRANCE_AVAILABILITY_STATUS_AVAILABLE)
                .setCreatedAt(toProtoTimestamp(Instant.ofEpochSecond(System.currentTimeMillis() / 1000)))
                .build();
    }

    public static FragranceData createFragranceWithAvailability(FragranceAvailabilityStatus availability) {
        return FragranceData.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setRating(5)
                .setName("Availability Test Fragrance")
                .setResume("Тестовый аромат")
                .addSeason(FragranceSeason.FRAGRANCE_SEASON_DAY)
                .setGender(FragranceGender.FRAGRANCE_GENDER_UNISEX)
                .addType(FragranceType.FRAGRANCE_TYPE_CITRUS)
                .setSillage(FragranceSillage.FRAGRANCE_SILLAGE_INTIMATE)
                .setLongevity(FragranceLongevity.FRAGRANCE_LONGEVITY_WEAK)
                .setAvailability(availability)
                .setCreatedAt(toProtoTimestamp(Instant.ofEpochSecond(System.currentTimeMillis() / 1000)))
                .build();
    }

    public static FragranceData createFragranceWithDefaultTimestamps() {
        return FragranceData.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setRating(6)
                .setName("Default Timestamps Fragrance")
                .setResume("Тестовый аромат без дат")
                .addSeason(FragranceSeason.FRAGRANCE_SEASON_SPRING)
                .setGender(FragranceGender.FRAGRANCE_GENDER_MALE)
                .addType(FragranceType.FRAGRANCE_TYPE_WOODY)
                .setSillage(FragranceSillage.FRAGRANCE_SILLAGE_MODERATE)
                .setLongevity(FragranceLongevity.FRAGRANCE_LONGEVITY_MIDDLE)
                .setAvailability(FragranceAvailabilityStatus.FRAGRANCE_AVAILABILITY_STATUS_AVAILABLE)
                .setCreatedAt(Timestamp.getDefaultInstance())
                .setUpdatedAt(Timestamp.getDefaultInstance())
                .build();
    }

    private static Timestamp toProtoTimestamp(Instant instant) {
        return Timestamp.newBuilder()
                .setSeconds(instant.getEpochSecond())
                .setNanos(instant.getNano())
                .build();
    }
}
