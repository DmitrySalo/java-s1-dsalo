package ru.my.scents.adapter.gateway.grpc.fragrance.strategy;

import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import ru.my.scents.adapter.gateway.grpc.fragrance.FragranceGatewayErrorHandler;
import ru.my.scents.domain.entity.fragrance.Fragrance;
import ru.my.scents.domain.entity.fragrance.FragranceID;
import ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.AlertResponse;
import ru.my.scents.infra.logger.Logger;

@Component
@RequiredArgsConstructor
public class ErrorHttpStatusStrategy {

    private final Map<HttpStatus, HandleStrategy> strategies = Map.of(
            HttpStatus.NOT_FOUND, notFoundStrategy(),
            HttpStatus.BAD_REQUEST, badRequestStrategy(),
            HttpStatus.INTERNAL_SERVER_ERROR, internalServerErrorStrategy()
    );

    private final FragranceGatewayErrorHandler errorHandler;
    private final Logger logger;

    public Optional<Fragrance> handle(AlertResponse error, FragranceID fragranceId) {
        var httpStatus = HttpStatus.valueOf(Integer.parseInt(error.getHttpCode()));

        return strategies
                .getOrDefault(httpStatus, defaultStrategy())
                .handle(error, fragranceId);
    }

    private HandleStrategy notFoundStrategy() {
        return (error, fragranceId) -> {
            logger.error("Парфюм {} не найден во внешнем сервисе: {} ({})",
                    fragranceId.getValue(), error.getMsg(), error.getHttpCode());

            return Optional.empty();
        };
    }

    private HandleStrategy badRequestStrategy() {
        return (error, fragranceId) -> {
            throw errorHandler.businessErrorReceived(fragranceId, error.getHttpCode(), error.getMsg());
        };
    }

    private HandleStrategy internalServerErrorStrategy() {
        return (error, fragranceId) -> {
            throw errorHandler.internalServerErrorReceived(fragranceId, error.getHttpCode(), error.getMsg());
        };
    }

    private HandleStrategy defaultStrategy() {
        return (error, fragranceId) -> {
            throw errorHandler.unexpectedErrorReceived(fragranceId, error.getHttpCode(), error.getMsg());
        };
    }

    @FunctionalInterface
    private interface HandleStrategy {

        Optional<Fragrance> handle(AlertResponse error, FragranceID fragranceId);
    }
}