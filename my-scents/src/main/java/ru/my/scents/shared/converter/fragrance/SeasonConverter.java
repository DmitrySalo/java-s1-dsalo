package ru.my.scents.shared.converter.fragrance;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;
import ru.my.scents.domain.entity.fragrance.FragranceSeason;
import static ru.my.scents.domain.entity.fragrance.FragranceSeason.DAY;
import static ru.my.scents.domain.entity.fragrance.FragranceSeason.FALL;
import static ru.my.scents.domain.entity.fragrance.FragranceSeason.NIGHT;
import static ru.my.scents.domain.entity.fragrance.FragranceSeason.SPRING;
import static ru.my.scents.domain.entity.fragrance.FragranceSeason.SUMMER;
import static ru.my.scents.domain.entity.fragrance.FragranceSeason.WINTER;

@UtilityClass
public class SeasonConverter {

    private final Map<FragranceSeason, ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceSeason> fragranceSeasonMapProto =
            Map.ofEntries(
                    Map.entry(WINTER, ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceSeason.FRAGRANCE_SEASON_WINTER),
                    Map.entry(SPRING, ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceSeason.FRAGRANCE_SEASON_SPRING),
                    Map.entry(SUMMER, ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceSeason.FRAGRANCE_SEASON_SUMMER),
                    Map.entry(FALL, ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceSeason.FRAGRANCE_SEASON_FALL),
                    Map.entry(DAY, ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceSeason.FRAGRANCE_SEASON_DAY),
                    Map.entry(NIGHT, ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceSeason.FRAGRANCE_SEASON_NIGHT)
            );

    private final Map<ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceSeason, FragranceSeason> fragranceSeasonMap =
            Map.ofEntries(
                    Map.entry(ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceSeason.FRAGRANCE_SEASON_WINTER, WINTER),
                    Map.entry(ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceSeason.FRAGRANCE_SEASON_SPRING, SPRING),
                    Map.entry(ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceSeason.FRAGRANCE_SEASON_SUMMER, SUMMER),
                    Map.entry(ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceSeason.FRAGRANCE_SEASON_FALL, FALL),
                    Map.entry(ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceSeason.FRAGRANCE_SEASON_DAY, DAY),
                    Map.entry(ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceSeason.FRAGRANCE_SEASON_NIGHT, NIGHT)
            );

    public static Set<ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceSeason> toProtoSeasons(
            Collection<FragranceSeason> seasons
    ) {
        if (seasons == null || seasons.isEmpty()) {
            return Set.of();
        }

        return seasons.stream()
                .map(SeasonConverter::toProto)
                .collect(Collectors.toSet());
    }

    public static ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceSeason toProto(
            FragranceSeason season
    ) {
        if (season == null) {
            return ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceSeason.FRAGRANCE_SEASON_UNSPECIFIED;
        }

        return fragranceSeasonMapProto.get(season);
    }

    public static Set<FragranceSeason> toDomainSeasons(
            Collection<ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceSeason> seasons
    ) {
        if (seasons == null || seasons.isEmpty()) {
            return Set.of();
        }

        return seasons.stream()
                .map(SeasonConverter::toDomain)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    public static FragranceSeason toDomain(
            ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceSeason season
    ) {
        if (season == null
                || season == ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceSeason.FRAGRANCE_SEASON_UNSPECIFIED
                || season == ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceSeason.UNRECOGNIZED) {
            return null;
        }

        return fragranceSeasonMap.get(season);
    }

    public static ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceSeason toProto(
            String season
    ) {
        if (season == null || season.isBlank()) {
            return null;
        }

        var fragranceSeason = FragranceSeason.valueOf(season);
        return fragranceSeasonMapProto.get(fragranceSeason);
    }

    public static Set<ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceSeason> toProtoSeasonsFromString(
            Collection<String> seasons
    ) {
        if (seasons == null || seasons.isEmpty()) {
            return Set.of();
        }

        return seasons.stream()
                .map(FragranceSeason::valueOf)
                .map(fragranceSeasonMapProto::get)
                .collect(Collectors.toSet());
    }

    public static Set<String> toStringSeasons(
            Collection<ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceSeason> seasons
    ) {
        if (seasons == null || seasons.isEmpty()) {
            return Set.of();
        }

        return SeasonConverter.toDomainSeasons(seasons).stream()
                .map(Enum::name)
                .collect(Collectors.toSet());
    }

    public static String toString(
            ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceSeason season
    ) {
        return toDomain(season).name();
    }
}