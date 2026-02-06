package ru.my.scents.adapter.controller.grpc;

import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import ru.my.scents.adapter.controller.grpc.converter.FragranceProtoConverter;
import ru.my.scents.boundary.usecase.FragranceUseCase;
import ru.my.scents.domain.entity.fragrance.Fragrance;
import ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.AlertResponse;
import ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceServiceGrpc;
import ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.GetFragranceRequest;
import ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.GetFragranceResponse;
import ru.my.scents.infra.logger.Logger;

@Component
@RequiredArgsConstructor
public class FragranceGrpcController extends FragranceServiceGrpc.FragranceServiceImplBase {

    private final FragranceUseCase fragranceUseCase;
    private final Logger logger;

    @Override
    public void getFragrance(GetFragranceRequest request, StreamObserver<GetFragranceResponse> responseObserver) {
        try {
            Fragrance fragrance = fragranceUseCase.get(request.getFragranceId());
            var response = GetFragranceResponse.newBuilder()
                    .setData(FragranceProtoConverter.toProto(fragrance))
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (RuntimeException e) {
            AlertResponse alertResponse = AlertResponse.newBuilder()
                    .setMsg(e.getMessage())
                    .setHttpCode(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()))
                    .build();

            GetFragranceResponse response = GetFragranceResponse.newBuilder()
                    .setError(alertResponse)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            logger.error(e.getMessage());
            StatusRuntimeException response = FragranceGrpcControllerError.internalError(e);
            responseObserver.onError(response);
        }
    }
}
