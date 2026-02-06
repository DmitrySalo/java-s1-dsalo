package ru.my.scents.adapter.controller.grpc;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import lombok.experimental.UtilityClass;

@UtilityClass
public class FragranceGrpcControllerError {

    public static StatusRuntimeException internalError(Throwable cause) {
        String message = "Внутренняя ошибка сервера";
        return Status.INTERNAL
                .withDescription(message)
                .withCause(cause)
                .asRuntimeException();
    }
}
