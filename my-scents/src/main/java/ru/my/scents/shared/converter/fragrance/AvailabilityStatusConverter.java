package ru.my.scents.shared.converter.fragrance;

import java.util.Map;
import lombok.experimental.UtilityClass;
import ru.my.scents.domain.entity.fragrance.FragranceAvailabilityStatus;
import static ru.my.scents.domain.entity.fragrance.FragranceAvailabilityStatus.AVAILABLE;
import static ru.my.scents.domain.entity.fragrance.FragranceAvailabilityStatus.UNAVAILABLE;

@UtilityClass
public class AvailabilityStatusConverter {

    private final Map<FragranceAvailabilityStatus, ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceAvailabilityStatus> fragranceAvailabilityStatusProtoMap =
            Map.ofEntries(
                    Map.entry(AVAILABLE, ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceAvailabilityStatus.FRAGRANCE_AVAILABILITY_STATUS_AVAILABLE),
                    Map.entry(UNAVAILABLE, ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceAvailabilityStatus.FRAGRANCE_AVAILABILITY_STATUS_UNAVAILABLE)
            );

    private final Map<ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceAvailabilityStatus, FragranceAvailabilityStatus> fragranceAvailabilityStatusMap =
            Map.ofEntries(
                    Map.entry(ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceAvailabilityStatus.FRAGRANCE_AVAILABILITY_STATUS_AVAILABLE, AVAILABLE),
                    Map.entry(ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceAvailabilityStatus.FRAGRANCE_AVAILABILITY_STATUS_UNAVAILABLE, UNAVAILABLE)
            );

    public static ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceAvailabilityStatus toProto(
            FragranceAvailabilityStatus availability
    ) {
        if (availability == null) {
            return ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceAvailabilityStatus.FRAGRANCE_AVAILABILITY_STATUS_UNSPECIFIED;
        }

        return fragranceAvailabilityStatusProtoMap.get(availability);
    }

    public static FragranceAvailabilityStatus toDomain(
            ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceAvailabilityStatus availability
    ) {
        if (availability == null
                || availability == ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceAvailabilityStatus.FRAGRANCE_AVAILABILITY_STATUS_UNSPECIFIED
                || availability == ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceAvailabilityStatus.UNRECOGNIZED
        ) {
            return null;
        }

        return fragranceAvailabilityStatusMap.get(availability);
    }

    public static ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceAvailabilityStatus toProto(
            String availabilityStatus
    ) {
        if (availabilityStatus == null || availabilityStatus.isBlank()) {
            return null;
        }

        var fragranceAvailabilityStatus = FragranceAvailabilityStatus.valueOf(availabilityStatus);
        return fragranceAvailabilityStatusProtoMap.get(fragranceAvailabilityStatus);
    }

    public static String toString(
            ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceAvailabilityStatus availabilityStatus
    ) {
        return toDomain(availabilityStatus).name();
    }
}