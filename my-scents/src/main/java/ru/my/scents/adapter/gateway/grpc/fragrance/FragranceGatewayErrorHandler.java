package ru.my.scents.adapter.gateway.grpc.fragrance;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.my.scents.domain.entity.fragrance.FragranceID;
import ru.my.scents.infra.logger.Logger;

@Component
@RequiredArgsConstructor
public class FragranceGatewayErrorHandler {

    private final Logger logger;

    public RuntimeException grpcCallFailed(FragranceID userId, Throwable cause) {
        String message = String.format("Не удалось получить парфюм %s из внешнего сервиса", userId.getValue());
        return new RuntimeException(message, cause);
    }

    public RuntimeException unknownResponseType(FragranceID userId, String responseType) {
        String message = String.format("Неизвестный тип ответа от FragranceService для парфюма %s: %s",
                userId.getValue(), responseType);
        return new RuntimeException(message);
    }

    public RuntimeException businessErrorReceived(FragranceID userId, String code, String message) {
        String msg = String.format("Ошибка бизнес-логики FragranceService для парфюма %s: code=%s, message=%s",
                userId.getValue(), code, message);

        logger.error(msg);
        return new RuntimeException(msg);
    }

    public RuntimeException internalServerErrorReceived(FragranceID userId, String code, String message) {
        String msg = String.format("внутренняя ошибка FragranceService для парфюма %s: code=%s, message=%s",
                userId.getValue(), code, message);

        logger.error(msg);
        return new RuntimeException(msg);
    }

    public RuntimeException unexpectedErrorReceived(FragranceID userId, String code, String message) {
        String msg = String.format("Неожиданная ошибка FragranceService для парфюма %s: code=%s, message=%s",
                userId.getValue(), code, message);

        logger.error(msg);
        return new RuntimeException(msg);
    }

    public RuntimeException converterFailed(FragranceID userId, Throwable cause) {
        String msg = String.format("Ошибка преобразования парфюма %s из proto в domain", userId.getValue());

        logger.error(msg);
        return new RuntimeException(msg, cause);
    }
}