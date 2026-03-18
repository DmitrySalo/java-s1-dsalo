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
        if (request == null) {
            throw new IllegalArgumentException("Тело запроса обязательно!");
        }

        return CreateFragranceParam.builder()
                .id(UUID.randomUUID().toString())
                .name(request.name())
                .rating(request.rating())
                .resume(request.resume())
                .concentration(request.concentration())
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
        if (request == null) {
            throw new IllegalArgumentException("Тело запроса обязательно!");
        }

        return UpdateFragranceParam.builder()
                .id(request.id())
                .name(request.name())
                .rating(request.rating())
                .resume(request.resume())
                .concentration(request.concentration())
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
