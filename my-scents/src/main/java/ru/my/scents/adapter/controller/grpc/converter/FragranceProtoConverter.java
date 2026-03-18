package ru.my.scents.adapter.controller.grpc.converter;

import lombok.experimental.UtilityClass;
import ru.my.scents.domain.entity.fragrance.Fragrance;
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
}