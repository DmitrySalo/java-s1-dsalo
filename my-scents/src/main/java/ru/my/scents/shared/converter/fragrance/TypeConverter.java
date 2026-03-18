package ru.my.scents.shared.converter.fragrance;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;
import ru.my.scents.domain.entity.fragrance.FragranceType;
import static ru.my.scents.domain.entity.fragrance.FragranceType.ALDEHYDE;
import static ru.my.scents.domain.entity.fragrance.FragranceType.AMBER;
import static ru.my.scents.domain.entity.fragrance.FragranceType.ANIMALIC;
import static ru.my.scents.domain.entity.fragrance.FragranceType.AQUATIC;
import static ru.my.scents.domain.entity.fragrance.FragranceType.BALSAMIC;
import static ru.my.scents.domain.entity.fragrance.FragranceType.CHYPRE;
import static ru.my.scents.domain.entity.fragrance.FragranceType.CITRUS;
import static ru.my.scents.domain.entity.fragrance.FragranceType.FLORAL;
import static ru.my.scents.domain.entity.fragrance.FragranceType.FOUGERE;
import static ru.my.scents.domain.entity.fragrance.FragranceType.FRUITY;
import static ru.my.scents.domain.entity.fragrance.FragranceType.LEATHER;
import static ru.my.scents.domain.entity.fragrance.FragranceType.MUSKY;
import static ru.my.scents.domain.entity.fragrance.FragranceType.ORIENTAL;
import static ru.my.scents.domain.entity.fragrance.FragranceType.SMOKY;
import static ru.my.scents.domain.entity.fragrance.FragranceType.SPICY;
import static ru.my.scents.domain.entity.fragrance.FragranceType.VANILLA;
import static ru.my.scents.domain.entity.fragrance.FragranceType.WOODY;

@UtilityClass
public class TypeConverter {

    private final Map<FragranceType,
            ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceType> fragranceTypeProtoMap =
            Map.ofEntries(
                    Map.entry(ORIENTAL, ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceType.FRAGRANCE_TYPE_ORIENTAL),
                    Map.entry(FLORAL, ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceType.FRAGRANCE_TYPE_FLORAL),
                    Map.entry(CHYPRE, ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceType.FRAGRANCE_TYPE_CHYPRE),
                    Map.entry(FOUGERE, ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceType.FRAGRANCE_TYPE_FOUGERE),
                    Map.entry(WOODY, ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceType.FRAGRANCE_TYPE_WOODY),
                    Map.entry(AQUATIC, ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceType.FRAGRANCE_TYPE_AQUATIC),
                    Map.entry(FRUITY, ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceType.FRAGRANCE_TYPE_FRUITY),
                    Map.entry(CITRUS, ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceType.FRAGRANCE_TYPE_CITRUS),
                    Map.entry(VANILLA, ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceType.FRAGRANCE_TYPE_VANILLA),
                    Map.entry(AMBER, ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceType.FRAGRANCE_TYPE_AMBER),
                    Map.entry(LEATHER, ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceType.FRAGRANCE_TYPE_LEATHER),
                    Map.entry(ANIMALIC, ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceType.FRAGRANCE_TYPE_ANIMALIC),
                    Map.entry(SMOKY, ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceType.FRAGRANCE_TYPE_SMOKY),
                    Map.entry(BALSAMIC, ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceType.FRAGRANCE_TYPE_BALSAMIC),
                    Map.entry(MUSKY, ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceType.FRAGRANCE_TYPE_MUSKY),
                    Map.entry(SPICY, ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceType.FRAGRANCE_TYPE_SPICY),
                    Map.entry(ALDEHYDE, ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceType.FRAGRANCE_TYPE_ALDEHYDE)
            );

    private final Map<ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceType, FragranceType> fragranceTypeMap =
            Map.ofEntries(
                    Map.entry(ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceType.FRAGRANCE_TYPE_ORIENTAL, ORIENTAL),
                    Map.entry(ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceType.FRAGRANCE_TYPE_FLORAL, FLORAL),
                    Map.entry(ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceType.FRAGRANCE_TYPE_CHYPRE, CHYPRE),
                    Map.entry(ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceType.FRAGRANCE_TYPE_FOUGERE, FOUGERE),
                    Map.entry(ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceType.FRAGRANCE_TYPE_WOODY, WOODY),
                    Map.entry(ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceType.FRAGRANCE_TYPE_AQUATIC, AQUATIC),
                    Map.entry(ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceType.FRAGRANCE_TYPE_FRUITY, FRUITY),
                    Map.entry(ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceType.FRAGRANCE_TYPE_CITRUS, CITRUS),
                    Map.entry(ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceType.FRAGRANCE_TYPE_VANILLA, VANILLA),
                    Map.entry(ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceType.FRAGRANCE_TYPE_AMBER, AMBER),
                    Map.entry(ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceType.FRAGRANCE_TYPE_LEATHER, LEATHER),
                    Map.entry(ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceType.FRAGRANCE_TYPE_ANIMALIC, ANIMALIC),
                    Map.entry(ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceType.FRAGRANCE_TYPE_SMOKY, SMOKY),
                    Map.entry(ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceType.FRAGRANCE_TYPE_BALSAMIC, BALSAMIC),
                    Map.entry(ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceType.FRAGRANCE_TYPE_MUSKY, MUSKY),
                    Map.entry(ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceType.FRAGRANCE_TYPE_SPICY, SPICY),
                    Map.entry(ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceType.FRAGRANCE_TYPE_ALDEHYDE, ALDEHYDE)
            );

    public static Set<ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceType> toProtoTypes(
            Collection<FragranceType> types
    ) {
        if (types == null || types.isEmpty()) {
            return Set.of();
        }

        return types.stream()
                .map(TypeConverter::toProto)
                .collect(Collectors.toSet());
    }

    public static ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceType toProto(
            FragranceType type
    ) {
        if (type == null) {
            return ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceType.FRAGRANCE_TYPE_UNSPECIFIED;
        }

        return fragranceTypeProtoMap.get(type);
    }

    public static Set<FragranceType> toDomainTypes(
            Collection<ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceType> types) {
        if (types == null || types.isEmpty()) {
            return Set.of();
        }
        return types.stream()
                .map(TypeConverter::toDomain)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    public static FragranceType toDomain(
            ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceType type
    ) {
        if (type == null
                || type == ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceType.FRAGRANCE_TYPE_UNSPECIFIED
                || type == ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceType.UNRECOGNIZED
        ) {
            return null;
        }

        return fragranceTypeMap.get(type);
    }

    public static ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceType toProto(
            String type
    ) {
        if (type == null || type.isBlank()) {
            return null;
        }

        var fragranceType = FragranceType.valueOf(type);
        return fragranceTypeProtoMap.get(fragranceType);
    }

    public static Set<ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceType> toProtoTypesFromString(
            Collection<String> types
    ) {
        if (types == null || types.isEmpty()) {
            return Set.of();
        }

        return types.stream()
                .map(FragranceType::valueOf)
                .map(fragranceTypeProtoMap::get)
                .collect(Collectors.toSet());
    }

    public static Set<String> toStringTypes(
            Collection<ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceType> types
    ) {
        if (types == null || types.isEmpty()) {
            return Set.of();
        }

        return TypeConverter.toDomainTypes(types).stream()
                .map(Enum::name)
                .collect(Collectors.toSet());
    }

    public static String toString(
            ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceType type
    ) {
        return toDomain(type).name();
    }
}