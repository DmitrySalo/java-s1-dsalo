package ru.my.scents.adapter.gateway.grpc.fragrance;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import ru.my.scents.adapter.gateway.grpc.fragrance.converter.FragranceProtoConverter;
import ru.my.scents.boundary.gateway.FragranceGateway;
import ru.my.scents.domain.entity.fragrance.Fragrance;
import ru.my.scents.domain.entity.fragrance.FragranceID;
import ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.AlertResponse;
import ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceData;
import ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceServiceGrpc;
import ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.GetFragranceRequest;
import ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.GetFragranceResponse;
import ru.my.scents.infra.grpc.client.GrpcProperties;
import ru.my.scents.infra.logger.Logger;

@Component
public class FragranceGrpcGateway implements FragranceGateway {

    private final FragranceServiceGrpc.FragranceServiceBlockingV2Stub stub;
    private final FragranceGatewayErrorHandler errorHandler;
    private final GrpcProperties grpcProperties;
    private final Logger logger;

    public FragranceGrpcGateway(
            FragranceServiceGrpc.FragranceServiceBlockingV2Stub stub,
            FragranceGatewayErrorHandler errorHandler,
            @Qualifier("fragranceServiceProperties") GrpcProperties grpcProperties,
            Logger logger) {

        this.stub = stub;
        this.errorHandler = errorHandler;
        this.grpcProperties = grpcProperties;
        this.logger = logger;
    }

    public Optional<Fragrance> findById(FragranceID fragranceId) {
        GetFragranceRequest request = buildRequest(fragranceId);
        var reqStub = stub.withDeadlineAfter(grpcProperties.getDeadline());

        try {
            GetFragranceResponse response = reqStub.getFragrance(request);
            return handleResponse(response, fragranceId);
        } catch (RuntimeException re) {
            logger.error(re.getMessage());
            throw errorHandler.grpcCallFailed(fragranceId, re);
        }
    }

    private GetFragranceRequest buildRequest(FragranceID fragranceId) {
        return GetFragranceRequest.newBuilder()
                .setFragranceId(fragranceId.getValue().toString())
                .build();
    }

    private Optional<Fragrance> handleResponse(GetFragranceResponse response, FragranceID fragranceId) {
        return switch (response.getResultCase()) {
            case DATA -> handleDataResponse(response.getData(), fragranceId);
            case ERROR -> handleErrorResponse(response.getError(), fragranceId);
            default -> throw errorHandler.unknownResponseType(fragranceId, response.getResultCase().toString());
        };
    }

    private Optional<Fragrance> handleDataResponse(FragranceData data, FragranceID fragranceId) {
        try {
            Fragrance fragrance = FragranceProtoConverter.toDomain(data);
            return Optional.of(fragrance);
        } catch (RuntimeException e) {
            throw errorHandler.converterFailed(fragranceId, e);
        }
    }

    private Optional<Fragrance> handleErrorResponse(AlertResponse error, FragranceID fragranceId) {
        return switch (HttpStatus.valueOf(Integer.parseInt(error.getHttpCode()))) {
            case HttpStatus.NOT_FOUND -> {
                logger.error("Парфюм {} не найден во внешнем сервисе: {} ({})",
                        fragranceId.getValue(), error.getMsg(), error.getHttpCode());
                yield Optional.empty();
            }
            case HttpStatus.BAD_REQUEST ->
                    throw errorHandler.businessErrorReceived(fragranceId, error.getHttpCode(), error.getMsg());
            case HttpStatus.INTERNAL_SERVER_ERROR ->
                    throw errorHandler.internalServerErrorReceived(fragranceId, error.getHttpCode(), error.getMsg());
            default -> throw errorHandler.unexpectedErrorReceived(fragranceId, error.getHttpCode(), error.getMsg());
        };
    }
}
