package ru.my.scents.adapter.event.consumer.converter;

import lombok.experimental.UtilityClass;
import ru.my.scents.boundary.model.fragrance.CreateFragranceParam;
import ru.my.scents.fragrance.adapter.event.fragrance.proto.FragranceEvent;
import ru.my.scents.fragrance.adapter.event.fragrance.proto.FragranceEventPayload;
import ru.my.scents.shared.converter.TimestampConverter;
import ru.my.scents.shared.converter.fragrance.AvailabilityStatusConverter;
import ru.my.scents.shared.converter.fragrance.ConcentrationConverter;
import ru.my.scents.shared.converter.fragrance.GenderConverter;
import ru.my.scents.shared.converter.fragrance.LongevityConverter;
import ru.my.scents.shared.converter.fragrance.SeasonConverter;
import ru.my.scents.shared.converter.fragrance.SillageConverter;
import ru.my.scents.shared.converter.fragrance.TypeConverter;

@UtilityClass
public class CreateFragranceParamMapper {

    public CreateFragranceParam toParam(FragranceEvent event) {
        if (event == null) {
            throw new IllegalArgumentException("Событие обязательно!");
        }

        FragranceEventPayload payload = event.getPayload();
        return CreateFragranceParam.builder()
                .id(payload.getId())
                .name(payload.getName())
                .rating(payload.getRating() != 0 ? (byte) payload.getRating() : null)
                .resume(payload.getResume())
                .concentration(ConcentrationConverter.toString(payload.getConcentration()))
                .type(TypeConverter.toStringTypes(payload.getTypeList()))
                .gender(GenderConverter.toString(payload.getGender()))
                .season(SeasonConverter.toStringSeasons(payload.getSeasonList()))
                .longevity(LongevityConverter.toString(payload.getLongevity()))
                .sillage(SillageConverter.toString(payload.getSillage()))
                .availability(AvailabilityStatusConverter.toString(payload.getAvailability()))
                .createdAt(TimestampConverter.toInstant(payload.getCreatedAt()))
                .build();
    }
}
