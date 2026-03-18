package ru.my.scents.shared.converter.fragrance;

import java.util.Map;
import lombok.experimental.UtilityClass;
import ru.my.scents.domain.entity.fragrance.FragranceConcentration;
import static ru.my.scents.domain.entity.fragrance.FragranceConcentration.EAU_DE_COLOGNE;
import static ru.my.scents.domain.entity.fragrance.FragranceConcentration.EAU_DE_PARFUM;
import static ru.my.scents.domain.entity.fragrance.FragranceConcentration.EAU_DE_TOILETTE;
import static ru.my.scents.domain.entity.fragrance.FragranceConcentration.EXTRACT_DE_PARFUM;
import static ru.my.scents.domain.entity.fragrance.FragranceConcentration.PARFUM;

@UtilityClass
public class ConcentrationConverter {

    private final Map<FragranceConcentration, ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceConcentration> fragranceConcentrationProtoMap =
            Map.ofEntries(
                    Map.entry(EAU_DE_COLOGNE, ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceConcentration.FRAGRANCE_CONCENTRATION_EAU_DE_COLOGNE),
                    Map.entry(EAU_DE_TOILETTE, ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceConcentration.FRAGRANCE_CONCENTRATION_EAU_DE_TOILETTE),
                    Map.entry(EAU_DE_PARFUM, ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceConcentration.FRAGRANCE_CONCENTRATION_EAU_DE_PARFUM),
                    Map.entry(PARFUM, ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceConcentration.FRAGRANCE_CONCENTRATION_PARFUM),
                    Map.entry(EXTRACT_DE_PARFUM, ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceConcentration.FRAGRANCE_CONCENTRATION_EXTRACT_DE_PARFUM)
            );

    private final Map<ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceConcentration, FragranceConcentration> fragranceConcentrationMap =
            Map.ofEntries(
                    Map.entry(ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceConcentration.FRAGRANCE_CONCENTRATION_EAU_DE_COLOGNE, EAU_DE_COLOGNE),
                    Map.entry(ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceConcentration.FRAGRANCE_CONCENTRATION_EAU_DE_TOILETTE, EAU_DE_TOILETTE),
                    Map.entry(ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceConcentration.FRAGRANCE_CONCENTRATION_EAU_DE_PARFUM, EAU_DE_PARFUM),
                    Map.entry(ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceConcentration.FRAGRANCE_CONCENTRATION_PARFUM, PARFUM),
                    Map.entry(ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceConcentration.FRAGRANCE_CONCENTRATION_EXTRACT_DE_PARFUM, EXTRACT_DE_PARFUM)
            );

    public static ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceConcentration toProto(
            FragranceConcentration concentration
    ) {
        if (concentration == null) {
            return ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceConcentration.FRAGRANCE_CONCENTRATION_UNSPECIFIED;
        }

        return fragranceConcentrationProtoMap.get(concentration);
    }

    public static FragranceConcentration toDomain(
            ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceConcentration concentration) {
        if (concentration == null
                || concentration == ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceConcentration.FRAGRANCE_CONCENTRATION_UNSPECIFIED
                || concentration == ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceConcentration.UNRECOGNIZED
        ) {
            return null;
        }
        return fragranceConcentrationMap.get(concentration);
    }

    public static ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceConcentration toProto(
            String concentration
    ) {
        if (concentration == null || concentration.isBlank()) {
            return null;
        }

        var fragranceGender = FragranceConcentration.valueOf(concentration);
        return fragranceConcentrationProtoMap.get(fragranceGender);
    }

    public static String toString(
            ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceConcentration concentration
    ) {
        return toDomain(concentration).name();
    }
}