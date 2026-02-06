package ru.my.scents.adapter.controller.http.fragrance.converter;

import java.util.Set;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;
import ru.my.scents.adapter.controller.http.fragrance.response.CreateFragranceResponse;
import ru.my.scents.adapter.controller.http.fragrance.response.GetFragranceResponse;
import ru.my.scents.adapter.controller.http.fragrance.response.UpdateFragranceResponse;
import ru.my.scents.domain.entity.fragrance.Fragrance;

@UtilityClass
public class ResponseFragranceConverter {

    public static CreateFragranceResponse createResultToResponse(Fragrance result) {
        return CreateFragranceResponse.builder()
                .id(result.getId().getValue().toString())
                .name(result.getName().getValue())
                .rating(result.getRating().getValue())
                .resume(result.getResume().getValue())
                .type(toStringSet(result.getType()))
                .gender(result.getGender().name())
                .season(toStringSet(result.getSeason()))
                .longevity(result.getLongevity().name())
                .sillage(result.getSillage().name())
                .availability(result.getAvailability().name())
                .build();
    }

    public static UpdateFragranceResponse updateResultToResponse(Fragrance result) {
        return UpdateFragranceResponse.builder()
                .id(result.getId().getValue().toString())
                .name(result.getName().getValue())
                .rating(result.getRating().getValue())
                .resume(result.getResume().getValue())
                .type(toStringSet(result.getType()))
                .gender(result.getGender().name())
                .season(toStringSet(result.getSeason()))
                .longevity(result.getLongevity().name())
                .sillage(result.getSillage().name())
                .availability(result.getAvailability().name())
                .build();
    }

    public static GetFragranceResponse getResultToResponse(Fragrance result) {
        return GetFragranceResponse.builder()
                .id(result.getId().getValue().toString())
                .name(result.getName().getValue())
                .rating(result.getRating().getValue())
                .resume(result.getResume().getValue())
                .type(toStringSet(result.getType()))
                .gender(result.getGender().name())
                .season(toStringSet(result.getSeason()))
                .longevity(result.getLongevity().name())
                .sillage(result.getSillage().name())
                .availability(result.getAvailability().name())
                .build();
    }

    private static Set<String> toStringSet(Set<? extends Enum<?>> enumSet) {
        return enumSet.stream()
                .map(Enum::name)
                .collect(Collectors.toSet());
    }
}