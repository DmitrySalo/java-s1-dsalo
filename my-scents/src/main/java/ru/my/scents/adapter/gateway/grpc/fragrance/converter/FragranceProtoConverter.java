package ru.my.scents.adapter.gateway.grpc.fragrance.converter;

import lombok.experimental.UtilityClass;
import ru.my.scents.domain.entity.fragrance.Fragrance;
import ru.my.scents.domain.entity.fragrance.FragranceID;
import ru.my.scents.domain.entity.fragrance.FragranceName;
import ru.my.scents.domain.entity.fragrance.FragranceRating;
import ru.my.scents.domain.entity.fragrance.FragranceResume;
import ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceData;
import ru.my.scents.shared.converter.TimestampConverter;
import ru.my.scents.shared.converter.fragrance.AvailabilityStatusConverter;
import ru.my.scents.shared.converter.fragrance.ConcentrationConverter;
import ru.my.scents.shared.converter.fragrance.GenderConverter;
import ru.my.scents.shared.converter.fragrance.LongevityConverter;
import ru.my.scents.shared.converter.fragrance.SeasonConverter;
import ru.my.scents.shared.converter.fragrance.SillageConverter;
import ru.my.scents.shared.converter.fragrance.TypeConverter;

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
                .setConcentration(ConcentrationConverter.toProto(fragrance.getConcentration()))
                .addAllType(TypeConverter.toProtoTypes(fragrance.getType()))
                .setGender(GenderConverter.toProto(fragrance.getGender()))
                .addAllSeason(SeasonConverter.toProtoSeasons(fragrance.getSeason()))
                .setLongevity(LongevityConverter.toProto(fragrance.getLongevity()))
                .setSillage(SillageConverter.toProto(fragrance.getSillage()))
                .setAvailability(AvailabilityStatusConverter.toProto(fragrance.getAvailability()));

        if (fragrance.getCreatedAt() != null) {
            builder.setCreatedAt(TimestampConverter.toProto(fragrance.getCreatedAt()));
        }
        if (fragrance.getUpdatedAt() != null) {
            builder.setUpdatedAt(TimestampConverter.toProto(fragrance.getUpdatedAt()));
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
                .concentration(ConcentrationConverter.toDomain(fragrance.getConcentration()))
                .type(TypeConverter.toDomainTypes(fragrance.getTypeList()))
                .gender(GenderConverter.toDomain(fragrance.getGender()))
                .season(SeasonConverter.toDomainSeasons(fragrance.getSeasonList()))
                .longevity(LongevityConverter.toDomain(fragrance.getLongevity()))
                .sillage(SillageConverter.toDomain(fragrance.getSillage()))
                .availability(AvailabilityStatusConverter.toDomain(fragrance.getAvailability()))
                .createdAt(TimestampConverter.toInstant(fragrance.getCreatedAt()))
                .updatedAt(TimestampConverter.toInstant(fragrance.getUpdatedAt()))
                .build();
    }
}
