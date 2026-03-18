package ru.my.scents.adapter.gateway.grpc.fragrance;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.my.scents.adapter.gateway.grpc.fragrance.strategy.ResponseCaseStrategy;
import ru.my.scents.boundary.gateway.FragranceGateway;
import ru.my.scents.domain.entity.fragrance.Fragrance;
import ru.my.scents.domain.entity.fragrance.FragranceID;
import ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceServiceGrpc;
import ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.GetFragranceRequest;
import ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.GetFragranceResponse;
import ru.my.scents.infra.grpc.client.GrpcProperties;
import ru.my.scents.infra.logger.Logger;

@Component
public class FragranceGrpcGateway implements FragranceGateway {

    private final FragranceServiceGrpc.FragranceServiceBlockingV2Stub stub;
    private final FragranceGatewayErrorHandler errorHandler;
    private final ResponseCaseStrategy responseCaseStrategy;
    private final GrpcProperties grpcProperties;
    private final Logger logger;

    public FragranceGrpcGateway(
            FragranceServiceGrpc.FragranceServiceBlockingV2Stub stub,
            FragranceGatewayErrorHandler errorHandler,
            ResponseCaseStrategy responseCaseStrategy,
            @Qualifier("fragranceServiceProperties") GrpcProperties grpcProperties,
            Logger logger
    ) {
        this.stub = stub;
        this.errorHandler = errorHandler;
        this.grpcProperties = grpcProperties;
        this.logger = logger;
        this.responseCaseStrategy = responseCaseStrategy;
    }

    @Override
    public Optional<Fragrance> findById(FragranceID fragranceId) {
        var request = buildRequest(fragranceId);
        var reqStub = stub.withDeadlineAfter(grpcProperties.getDeadline());

        try {
            GetFragranceResponse response = reqStub.getFragrance(request);
            return responseCaseStrategy.handle(response, fragranceId);
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
}