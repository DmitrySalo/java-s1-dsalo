package ru.my.scents.shared.converter.fragrance;

import java.util.Map;
import lombok.experimental.UtilityClass;
import ru.my.scents.domain.entity.fragrance.FragranceSillage;
import static ru.my.scents.domain.entity.fragrance.FragranceSillage.INTIMATE;
import static ru.my.scents.domain.entity.fragrance.FragranceSillage.MODERATE;
import static ru.my.scents.domain.entity.fragrance.FragranceSillage.VERY_STRONG;

@UtilityClass
public class SillageConverter {

    private final Map<FragranceSillage, ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceSillage> fragranceSillageProtoMap =
            Map.ofEntries(
                    Map.entry(INTIMATE, ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceSillage.FRAGRANCE_SILLAGE_INTIMATE),
                    Map.entry(MODERATE, ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceSillage.FRAGRANCE_SILLAGE_MODERATE),
                    Map.entry(FragranceSillage.STRONG, ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceSillage.FRAGRANCE_SILLAGE_STRONG),
                    Map.entry(VERY_STRONG, ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceSillage.FRAGRANCE_SILLAGE_VERY_STRONG)
            );

    private final Map<ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceSillage, FragranceSillage> fragranceSillageMap =
            Map.ofEntries(
                    Map.entry(ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceSillage.FRAGRANCE_SILLAGE_INTIMATE, INTIMATE),
                    Map.entry(ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceSillage.FRAGRANCE_SILLAGE_MODERATE, MODERATE),
                    Map.entry(ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceSillage.FRAGRANCE_SILLAGE_STRONG, FragranceSillage.STRONG),
                    Map.entry(ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceSillage.FRAGRANCE_SILLAGE_VERY_STRONG, VERY_STRONG)
            );

    public static ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceSillage toProto(
            FragranceSillage sillage
    ) {
        if (sillage == null) {
            return ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceSillage.FRAGRANCE_SILLAGE_UNSPECIFIED;
        }

        return fragranceSillageProtoMap.get(sillage);
    }

    public static FragranceSillage toDomain(
            ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceSillage sillage
    ) {
        if (sillage == null
                || sillage == ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceSillage.FRAGRANCE_SILLAGE_UNSPECIFIED
                || sillage == ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceSillage.UNRECOGNIZED
        ) {
            return null;
        }

        return fragranceSillageMap.get(sillage);
    }

    public static ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceSillage toProto(
            String sillage
    ) {
        if (sillage == null || sillage.isBlank()) {
            return null;
        }

        var fragranceSillage = FragranceSillage.valueOf(sillage);
        return fragranceSillageProtoMap.get(fragranceSillage);
    }

    public static String toString(
            ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceSillage type
    ) {
        return toDomain(type).name();
    }
}