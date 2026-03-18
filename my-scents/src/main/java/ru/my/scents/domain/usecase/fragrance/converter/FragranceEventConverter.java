package ru.my.scents.domain.usecase.fragrance.converter;

import java.time.Instant;
import java.util.UUID;
import lombok.experimental.UtilityClass;
import ru.my.scents.domain.entity.fragrance.Fragrance;
import ru.my.scents.fragrance.adapter.event.fragrance.proto.FragranceEvent;
import ru.my.scents.fragrance.adapter.event.fragrance.proto.FragranceEventPayload;
import ru.my.scents.fragrance.adapter.event.fragrance.proto.FragranceEventType;
import ru.my.scents.shared.converter.TimestampConverter;
import ru.my.scents.shared.converter.fragrance.AvailabilityStatusConverter;
import ru.my.scents.shared.converter.fragrance.ConcentrationConverter;
import ru.my.scents.shared.converter.fragrance.GenderConverter;
import ru.my.scents.shared.converter.fragrance.LongevityConverter;
import ru.my.scents.shared.converter.fragrance.SeasonConverter;
import ru.my.scents.shared.converter.fragrance.SillageConverter;
import ru.my.scents.shared.converter.fragrance.TypeConverter;

@UtilityClass
public class FragranceEventConverter {

    public static FragranceEvent toCreateEvent(Fragrance fragrance) {
        return toEvent(fragrance, FragranceEventType.FRAGRANCE_EVENT_TYPE_CREATED);
    }

    public static FragranceEvent toUpdateEvent(Fragrance fragrance) {
        return toEvent(fragrance, FragranceEventType.FRAGRANCE_EVENT_TYPE_UPDATED);
    }

    private static FragranceEvent toEvent(Fragrance fragrance, FragranceEventType eventType) {
        if (fragrance == null) {
            throw new IllegalArgumentException("Парфюм обязателен!");
        }

        return FragranceEvent.newBuilder()
                .setEventId(UUID.randomUUID().toString())
                .setEventType(eventType)
                .setEventTimestamp(TimestampConverter.toProto(Instant.now()))
                .setPayload(toPayload(fragrance))
                .build();
    }

    public static FragranceEvent toDeleteEvent(String fragranceId) {

        FragranceEventPayload payload = FragranceEventPayload.newBuilder()
                .setId(fragranceId)
                .build();

        return FragranceEvent.newBuilder()
                .setEventId(UUID.randomUUID().toString())
                .setEventType(FragranceEventType.FRAGRANCE_EVENT_TYPE_DELETED)
                .setEventTimestamp(TimestampConverter.toProto(Instant.now()))
                .setPayload(payload)
                .build();
    }

    private static FragranceEventPayload toPayload(Fragrance fragrance) {
        FragranceEventPayload.Builder builder = FragranceEventPayload.newBuilder()
                .setId(fragrance.getId().getValue().toString())
                .setName(fragrance.getName().getValue())
                .setResume(fragrance.getResume().getValue())
                .setConcentration(ConcentrationConverter.toProto(fragrance.getConcentration()))
                .addAllType(TypeConverter.toProtoTypes(fragrance.getType()))
                .addAllSeason(SeasonConverter.toProtoSeasons(fragrance.getSeason()))
                .setRating(fragrance.getRating().getValue())
                .setGender(GenderConverter.toProto(fragrance.getGender()))
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
