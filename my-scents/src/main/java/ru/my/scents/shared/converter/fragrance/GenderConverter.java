package ru.my.scents.shared.converter.fragrance;

import java.util.Map;
import lombok.experimental.UtilityClass;
import ru.my.scents.domain.entity.fragrance.FragranceGender;
import static ru.my.scents.domain.entity.fragrance.FragranceGender.FEMALE;
import static ru.my.scents.domain.entity.fragrance.FragranceGender.MALE;
import static ru.my.scents.domain.entity.fragrance.FragranceGender.UNISEX;

@UtilityClass
public class GenderConverter {

    private final Map<FragranceGender, ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceGender> fragranceGenderMapProto =
            Map.ofEntries(
                    Map.entry(MALE, ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceGender.FRAGRANCE_GENDER_MALE),
                    Map.entry(FEMALE, ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceGender.FRAGRANCE_GENDER_FEMALE),
                    Map.entry(UNISEX, ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceGender.FRAGRANCE_GENDER_UNISEX)
            );

    private final Map<ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceGender, FragranceGender> fragranceGenderMap =
            Map.ofEntries(
                    Map.entry(ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceGender.FRAGRANCE_GENDER_MALE, MALE),
                    Map.entry(ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceGender.FRAGRANCE_GENDER_FEMALE, FEMALE),
                    Map.entry(ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceGender.FRAGRANCE_GENDER_UNISEX, UNISEX)
            );

    public static ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceGender toProto(
            FragranceGender gender
    ) {
        if (gender == null) {
            return ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceGender.FRAGRANCE_GENDER_UNSPECIFIED;
        }

        return fragranceGenderMapProto.get(gender);
    }

    public static FragranceGender toDomain(
            ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceGender gender
    ) {
        if (gender == null || gender == ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceGender.FRAGRANCE_GENDER_UNSPECIFIED) {
            return null;
        }

        return fragranceGenderMap.get(gender);
    }

    public static ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceGender toProto(
            String gender
    ) {
        if (gender == null || gender.isBlank()) {
            return null;
        }

        var fragranceGender = FragranceGender.valueOf(gender);
        return fragranceGenderMapProto.get(fragranceGender);
    }

    public static String toString(
            ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceGender gender
    ) {
        return toDomain(gender).name();
    }
}