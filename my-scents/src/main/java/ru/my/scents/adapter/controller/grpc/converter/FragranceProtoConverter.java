package ru.my.scents.adapter.controller.grpc.converter;

import com.google.protobuf.Timestamp;
import java.time.Instant;
import java.util.Objects;
import java.util.Set;
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
import ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceData;

@UtilityClass
public class FragranceProtoConverter {

    public static FragranceData toProto(Fragrance fragrance) {
        if (fragrance == null) {
            throw new IllegalArgumentException("Парфюм обязателен!");
        }

        var builder = FragranceData.newBuilder()
                .setId(fragrance.getId().getValue().toString())
                .setName(fragrance.getName().getValue())
                .setRating(fragrance.getRating().getValue())
                .setResume(fragrance.getResume().getValue())
                .addAllType(toProtoTypes(fragrance.getType()))
                .setGender(toProto(fragrance.getGender()))
                .addAllSeason(toProtoSeasons(fragrance.getSeason()))
                .setLongevity(toProto(fragrance.getLongevity()))
                .setSillage(toProto(fragrance.getSillage()))
                .setAvailability(toProto(fragrance.getAvailability()));

        if (fragrance.getCreatedAt() != null) {
            builder.setCreatedAt(toProto(fragrance.getCreatedAt()));
        }
        if (fragrance.getUpdatedAt() != null) {
            builder.setUpdatedAt(toProto(fragrance.getUpdatedAt()));
        }

        return builder.build();
    }

    public static Fragrance toDomain(FragranceData fragrance) {
        if (fragrance == null) {
            throw new IllegalArgumentException("Парфюм обязателен!");
        }

        return Fragrance.builder()
                .id(FragranceID.of(fragrance.getId()))
                .name(FragranceName.of(fragrance.getName()))
                .rating(FragranceRating.of((byte) fragrance.getRating()))
                .resume(FragranceResume.of(fragrance.getResume()))
                .type(toDomainTypes(fragrance.getTypeList()))
                .gender(toDomain(fragrance.getGender()))
                .season(toDomainSeasons(fragrance.getSeasonList()))
                .longevity(toDomain(fragrance.getLongevity()))
                .sillage(toDomain(fragrance.getSillage()))
                .availability(toDomain(fragrance.getAvailability()))
                .createdAt(toDomain(fragrance.getCreatedAt()))
                .updatedAt(toDomain(fragrance.getUpdatedAt()))
                .build();
    }

    private static Timestamp toProto(Instant instant) {
        if (instant == null) {
            return Timestamp.getDefaultInstance();
        }
        return Timestamp.newBuilder()
                .setSeconds(instant.getEpochSecond())
                .setNanos(instant.getNano())
                .build();
    }

    private static Instant toDomain(Timestamp timestamp) {
        if (timestamp == null || timestamp.equals(Timestamp.getDefaultInstance())) {
            return null;
        }
        return Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos());
    }

    private static Set<ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceType> toProtoTypes(
            Set<FragranceType> types) {
        if (types == null || types.isEmpty()) {
            return Set.of();
        }
        return types.stream()
                .map(FragranceProtoConverter::toProto)
                .collect(Collectors.toSet());
    }

    private static ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceType toProto(
            FragranceType type) {
        if (type == null) {
            return ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceType.FRAGRANCE_TYPE_UNSPECIFIED;
        }
        return switch (type) {
            case ORIENTAL ->
                    ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceType.FRAGRANCE_TYPE_ORIENTAL;
            case FLORAL ->
                    ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceType.FRAGRANCE_TYPE_FLORAL;
            case CHYPRE ->
                    ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceType.FRAGRANCE_TYPE_CHYPRE;
            case FOUGERE ->
                    ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceType.FRAGRANCE_TYPE_FOUGERE;
            case WOODY ->
                    ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceType.FRAGRANCE_TYPE_WOODY;
            case AQUATIC ->
                    ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceType.FRAGRANCE_TYPE_AQUATIC;
            case FRUITY ->
                    ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceType.FRAGRANCE_TYPE_FRUITY;
            case CITRUS ->
                    ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceType.FRAGRANCE_TYPE_CITRUS;
            case VANILLA ->
                    ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceType.FRAGRANCE_TYPE_VANILLA;
            case AMBER ->
                    ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceType.FRAGRANCE_TYPE_AMBER;
            case LEATHER ->
                    ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceType.FRAGRANCE_TYPE_LEATHER;
            case ANIMALIC ->
                    ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceType.FRAGRANCE_TYPE_ANIMALIC;
            case SMOKY ->
                    ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceType.FRAGRANCE_TYPE_SMOKY;
            case BALSAMIC ->
                    ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceType.FRAGRANCE_TYPE_BALSAMIC;
            case MUSKY ->
                    ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceType.FRAGRANCE_TYPE_MUSKY;
            case SPICY ->
                    ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceType.FRAGRANCE_TYPE_SPICY;
            case ALDEHYDE ->
                    ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceType.FRAGRANCE_TYPE_ALDEHYDE;
        };
    }

    private static Set<FragranceType> toDomainTypes(
            java.util.List<ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceType> types) {
        if (types == null || types.isEmpty()) {
            return Set.of();
        }
        return types.stream()
                .map(FragranceProtoConverter::toDomain)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    private static FragranceType toDomain(
            ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceType type) {
        if (type == null) {
            return null;
        }
        return switch (type) {
            case FRAGRANCE_TYPE_ORIENTAL -> FragranceType.ORIENTAL;
            case FRAGRANCE_TYPE_FLORAL -> FragranceType.FLORAL;
            case FRAGRANCE_TYPE_CHYPRE -> FragranceType.CHYPRE;
            case FRAGRANCE_TYPE_FOUGERE -> FragranceType.FOUGERE;
            case FRAGRANCE_TYPE_WOODY -> FragranceType.WOODY;
            case FRAGRANCE_TYPE_AQUATIC -> FragranceType.AQUATIC;
            case FRAGRANCE_TYPE_FRUITY -> FragranceType.FRUITY;
            case FRAGRANCE_TYPE_CITRUS -> FragranceType.CITRUS;
            case FRAGRANCE_TYPE_VANILLA -> FragranceType.VANILLA;
            case FRAGRANCE_TYPE_AMBER -> FragranceType.AMBER;
            case FRAGRANCE_TYPE_LEATHER -> FragranceType.LEATHER;
            case FRAGRANCE_TYPE_ANIMALIC -> FragranceType.ANIMALIC;
            case FRAGRANCE_TYPE_SMOKY -> FragranceType.SMOKY;
            case FRAGRANCE_TYPE_BALSAMIC -> FragranceType.BALSAMIC;
            case FRAGRANCE_TYPE_MUSKY -> FragranceType.MUSKY;
            case FRAGRANCE_TYPE_SPICY -> FragranceType.SPICY;
            case FRAGRANCE_TYPE_ALDEHYDE -> FragranceType.ALDEHYDE;
            case FRAGRANCE_TYPE_UNSPECIFIED, UNRECOGNIZED -> null;
        };
    }

    private static ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceGender toProto(
            FragranceGender gender) {
        if (gender == null) {
            return ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceGender.FRAGRANCE_GENDER_UNSPECIFIED;
        }
        return switch (gender) {
            case MALE ->
                    ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceGender.FRAGRANCE_GENDER_MALE;
            case FEMALE ->
                    ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceGender.FRAGRANCE_GENDER_FEMALE;
            case UNISEX ->
                    ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceGender.FRAGRANCE_GENDER_UNISEX;
        };
    }

    private static FragranceGender toDomain(
            ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceGender gender) {
        if (gender == null) {
            return null;
        }
        return switch (gender) {
            case FRAGRANCE_GENDER_MALE -> FragranceGender.MALE;
            case FRAGRANCE_GENDER_FEMALE -> FragranceGender.FEMALE;
            case FRAGRANCE_GENDER_UNISEX -> FragranceGender.UNISEX;
            case FRAGRANCE_GENDER_UNSPECIFIED, UNRECOGNIZED -> null;
        };
    }

    private static Set<ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceSeason> toProtoSeasons(
            Set<FragranceSeason> seasons) {
        if (seasons == null || seasons.isEmpty()) {
            return Set.of();
        }
        return seasons.stream()
                .map(FragranceProtoConverter::toProto)
                .collect(Collectors.toSet());
    }

    private static ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceSeason toProto(
            FragranceSeason season) {
        if (season == null) {
            return ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceSeason.FRAGRANCE_SEASON_UNSPECIFIED;
        }
        return switch (season) {
            case SPRING ->
                    ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceSeason.FRAGRANCE_SEASON_SPRING;
            case SUMMER ->
                    ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceSeason.FRAGRANCE_SEASON_SUMMER;
            case FALL ->
                    ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceSeason.FRAGRANCE_SEASON_FALL;
            case WINTER ->
                    ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceSeason.FRAGRANCE_SEASON_WINTER;
            case DAY ->
                    ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceSeason.FRAGRANCE_SEASON_DAY;
            case NIGHT ->
                    ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceSeason.FRAGRANCE_SEASON_NIGHT;
        };
    }

    private static Set<FragranceSeason> toDomainSeasons(
            java.util.List<ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceSeason> seasons) {
        if (seasons == null || seasons.isEmpty()) {
            return Set.of();
        }
        return seasons.stream()
                .map(FragranceProtoConverter::toDomain)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    private static FragranceSeason toDomain(
            ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceSeason season) {
        if (season == null) {
            return null;
        }
        return switch (season) {
            case FRAGRANCE_SEASON_SPRING -> FragranceSeason.SPRING;
            case FRAGRANCE_SEASON_SUMMER -> FragranceSeason.SUMMER;
            case FRAGRANCE_SEASON_FALL -> FragranceSeason.FALL;
            case FRAGRANCE_SEASON_WINTER -> FragranceSeason.WINTER;
            case FRAGRANCE_SEASON_DAY -> FragranceSeason.DAY;
            case FRAGRANCE_SEASON_NIGHT -> FragranceSeason.NIGHT;
            case FRAGRANCE_SEASON_UNSPECIFIED, UNRECOGNIZED -> null;
        };
    }

    private static ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceLongevity toProto(
            FragranceLongevity longevity) {
        if (longevity == null) {
            return ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceLongevity.FRAGRANCE_LONGEVITY_UNSPECIFIED;
        }
        return switch (longevity) {
            case VERY_WEAK ->
                    ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceLongevity.FRAGRANCE_LONGEVITY_VERY_WEAK;
            case WEAK ->
                    ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceLongevity.FRAGRANCE_LONGEVITY_WEAK;
            case MIDDLE ->
                    ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceLongevity.FRAGRANCE_LONGEVITY_MIDDLE;
            case STRONG ->
                    ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceLongevity.FRAGRANCE_LONGEVITY_STRONG;
            case ETERNAL ->
                    ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceLongevity.FRAGRANCE_LONGEVITY_ETERNAL;
        };
    }

    private static FragranceLongevity toDomain(
            ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceLongevity longevity) {
        if (longevity == null) {
            return null;
        }
        return switch (longevity) {
            case FRAGRANCE_LONGEVITY_VERY_WEAK -> FragranceLongevity.VERY_WEAK;
            case FRAGRANCE_LONGEVITY_WEAK -> FragranceLongevity.WEAK;
            case FRAGRANCE_LONGEVITY_MIDDLE -> FragranceLongevity.MIDDLE;
            case FRAGRANCE_LONGEVITY_STRONG -> FragranceLongevity.STRONG;
            case FRAGRANCE_LONGEVITY_ETERNAL -> FragranceLongevity.ETERNAL;
            case FRAGRANCE_LONGEVITY_UNSPECIFIED, UNRECOGNIZED -> null;
        };
    }

    private static ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceSillage toProto(
            FragranceSillage sillage) {
        if (sillage == null) {
            return ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceSillage.FRAGRANCE_SILLAGE_UNSPECIFIED;
        }
        return switch (sillage) {
            case INTIMATE ->
                    ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceSillage.FRAGRANCE_SILLAGE_INTIMATE;
            case MODERATE ->
                    ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceSillage.FRAGRANCE_SILLAGE_MODERATE;
            case STRONG ->
                    ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceSillage.FRAGRANCE_SILLAGE_STRONG;
            case VERY_STRONG ->
                    ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceSillage.FRAGRANCE_SILLAGE_VERY_STRONG;
        };
    }

    private static FragranceSillage toDomain(
            ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceSillage sillage) {
        if (sillage == null) {
            return null;
        }
        return switch (sillage) {
            case FRAGRANCE_SILLAGE_INTIMATE -> FragranceSillage.INTIMATE;
            case FRAGRANCE_SILLAGE_MODERATE -> FragranceSillage.MODERATE;
            case FRAGRANCE_SILLAGE_STRONG -> FragranceSillage.STRONG;
            case FRAGRANCE_SILLAGE_VERY_STRONG -> FragranceSillage.VERY_STRONG;
            case FRAGRANCE_SILLAGE_UNSPECIFIED, UNRECOGNIZED -> null;
        };
    }

    private static ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceAvailabilityStatus toProto(
            FragranceAvailabilityStatus availability) {
        if (availability == null) {
            return ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceAvailabilityStatus.FRAGRANCE_AVAILABILITY_STATUS_UNSPECIFIED;
        }
        return switch (availability) {
            case AVAILABLE ->
                    ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceAvailabilityStatus.FRAGRANCE_AVAILABILITY_STATUS_AVAILABLE;
            case UNAVAILABLE ->
                    ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceAvailabilityStatus.FRAGRANCE_AVAILABILITY_STATUS_UNAVAILABLE;
        };
    }

    private static FragranceAvailabilityStatus toDomain(
            ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceAvailabilityStatus availability) {
        if (availability == null) {
            return null;
        }
        return switch (availability) {
            case FRAGRANCE_AVAILABILITY_STATUS_AVAILABLE -> FragranceAvailabilityStatus.AVAILABLE;
            case FRAGRANCE_AVAILABILITY_STATUS_UNAVAILABLE -> FragranceAvailabilityStatus.UNAVAILABLE;
            case FRAGRANCE_AVAILABILITY_STATUS_UNSPECIFIED, UNRECOGNIZED -> null;
        };
    }
}