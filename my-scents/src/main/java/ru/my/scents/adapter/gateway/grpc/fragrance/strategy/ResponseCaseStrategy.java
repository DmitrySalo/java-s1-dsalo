package ru.my.scents.adapter.gateway.grpc.fragrance.strategy;

import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.my.scents.adapter.gateway.grpc.fragrance.FragranceGatewayErrorHandler;
import ru.my.scents.adapter.gateway.grpc.fragrance.converter.FragranceProtoConverter;
import ru.my.scents.domain.entity.fragrance.Fragrance;
import ru.my.scents.domain.entity.fragrance.FragranceID;
import ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.GetFragranceResponse;

@Component
@RequiredArgsConstructor
public class ResponseCaseStrategy {

    private final Map<GetFragranceResponse.ResultCase, HandleStrategy> strategies = Map.of(
            GetFragranceResponse.ResultCase.DATA, dataStrategy(),
            GetFragranceResponse.ResultCase.ERROR, errorStrategy()
    );

    private final FragranceGatewayErrorHandler errorHandler;
    private final ErrorHttpStatusStrategy statusStrategy;

    public Optional<Fragrance> handle(GetFragranceResponse response, FragranceID fragranceId) {
        return strategies
                .getOrDefault(response.getResultCase(), defaultStrategy())
                .handle(response, fragranceId);
    }

    private HandleStrategy defaultStrategy() {
        return (response, fragranceId) -> {
            throw errorHandler.unknownResponseType(fragranceId, response.getResultCase().toString());
        };
    }

    private HandleStrategy dataStrategy() {
        return (response, fragranceId) -> {
            try {
                Fragrance fragrance = FragranceProtoConverter.toDomain(response.getData());
                return Optional.of(fragrance);
            } catch (RuntimeException e) {
                throw errorHandler.converterFailed(fragranceId, e);
            }
        };
    }

    private HandleStrategy errorStrategy() {
        return (response, fragranceId) ->
                statusStrategy.handle(response.getError(), fragranceId);
    }

    @FunctionalInterface
    private interface HandleStrategy {

        Optional<Fragrance> handle(GetFragranceResponse response, FragranceID fragranceId);
    }
}