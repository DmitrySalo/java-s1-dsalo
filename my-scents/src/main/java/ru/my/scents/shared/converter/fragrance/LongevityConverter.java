package ru.my.scents.shared.converter.fragrance;

import java.util.Map;
import lombok.experimental.UtilityClass;
import ru.my.scents.domain.entity.fragrance.FragranceLongevity;
import static ru.my.scents.domain.entity.fragrance.FragranceLongevity.ETERNAL;
import static ru.my.scents.domain.entity.fragrance.FragranceLongevity.MIDDLE;
import static ru.my.scents.domain.entity.fragrance.FragranceLongevity.STRONG;
import static ru.my.scents.domain.entity.fragrance.FragranceLongevity.VERY_WEAK;
import static ru.my.scents.domain.entity.fragrance.FragranceLongevity.WEAK;

@UtilityClass
public class LongevityConverter {

    private final Map<FragranceLongevity, ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceLongevity> fragranceLongevityProtoMap =
            Map.ofEntries(
                    Map.entry(VERY_WEAK, ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceLongevity.FRAGRANCE_LONGEVITY_VERY_WEAK),
                    Map.entry(WEAK, ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceLongevity.FRAGRANCE_LONGEVITY_WEAK),
                    Map.entry(MIDDLE, ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceLongevity.FRAGRANCE_LONGEVITY_MIDDLE),
                    Map.entry(STRONG, ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceLongevity.FRAGRANCE_LONGEVITY_STRONG),
                    Map.entry(ETERNAL, ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceLongevity.FRAGRANCE_LONGEVITY_ETERNAL)
            );

    private final Map<ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceLongevity, FragranceLongevity> fragranceLongevityMap =
            Map.ofEntries(
                    Map.entry(ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceLongevity.FRAGRANCE_LONGEVITY_VERY_WEAK, VERY_WEAK),
                    Map.entry(ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceLongevity.FRAGRANCE_LONGEVITY_WEAK, WEAK),
                    Map.entry(ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceLongevity.FRAGRANCE_LONGEVITY_MIDDLE, MIDDLE),
                    Map.entry(ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceLongevity.FRAGRANCE_LONGEVITY_STRONG, STRONG),
                    Map.entry(ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceLongevity.FRAGRANCE_LONGEVITY_ETERNAL, ETERNAL)
            );

    public static ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceLongevity toProto(
            FragranceLongevity longevity
    ) {
        if (longevity == null) {
            return ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceLongevity.FRAGRANCE_LONGEVITY_UNSPECIFIED;
        }

        return fragranceLongevityProtoMap.get(longevity);
    }

    public static FragranceLongevity toDomain(
            ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceLongevity longevity
    ) {
        if (longevity == null
                || longevity == ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceLongevity.FRAGRANCE_LONGEVITY_UNSPECIFIED
                || longevity == ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceLongevity.UNRECOGNIZED
        ) {
            return null;
        }

        return fragranceLongevityMap.get(longevity);
    }

    public static ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceLongevity toProto(
            String longevity
    ) {
        if (longevity == null || longevity.isBlank()) {
            return null;
        }

        var fragranceLongevity = FragranceLongevity.valueOf(longevity);
        return fragranceLongevityProtoMap.get(fragranceLongevity);
    }

    public static String toString(
            ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceLongevity longevity
    ) {
        return toDomain(longevity).name();
    }
}