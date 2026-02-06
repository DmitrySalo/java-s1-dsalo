package ru.my.scents.adapter.controller.http.fragrance.converter;

import java.time.Instant;
import java.util.UUID;
import lombok.experimental.UtilityClass;
import ru.my.scents.adapter.controller.http.fragrance.request.CreateFragranceRequest;
import ru.my.scents.adapter.controller.http.fragrance.request.UpdateFragranceRequest;
import ru.my.scents.boundary.model.fragrance.CreateFragranceParam;
import ru.my.scents.boundary.model.fragrance.UpdateFragranceParam;

@UtilityClass
public class RequestFragranceConverter {

    public static CreateFragranceParam createRequestToModel(CreateFragranceRequest request) {
        return CreateFragranceParam.builder()
                .id(UUID.randomUUID().toString())
                .name(request.name())
                .rating(request.rating())
                .resume(request.resume())
                .type(request.type())
                .gender(request.gender())
                .season(request.season())
                .longevity(request.longevity())
                .sillage(request.sillage())
                .availability(request.availability())
                .createdAt(Instant.now())
                .build();
    }

    public static UpdateFragranceParam updateRequestToModel(UpdateFragranceRequest request) {
        return UpdateFragranceParam.builder()
                .id(request.id())
                .name(request.name())
                .rating(request.rating())
                .resume(request.resume())
                .type(request.type())
                .gender(request.gender())
                .season(request.season())
                .longevity(request.longevity())
                .sillage(request.sillage())
                .availability(request.availability())
                .updatedAt(Instant.now())
                .build();
    }
}
