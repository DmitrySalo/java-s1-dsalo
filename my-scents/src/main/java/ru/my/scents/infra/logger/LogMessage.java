package ru.my.scents.infra.logger;

import java.util.Map;
import java.util.function.BiConsumer;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;

@Builder(access = AccessLevel.PRIVATE)
class LogMessage {

    @Getter
    private final MessageType messageType;
    private final String message;
    private final Throwable throwable;
    private final Object[] args;
    private final Map<String, String> mdcContext = MDC.getCopyOfContextMap();

    static LogMessage error(String message, Throwable throwable, Object... args) {
        return LogMessage.builder()
                .messageType(MessageType.ERROR)
                .message(message)
                .throwable(throwable)
                .args(args)
                .build();
    }

    static LogMessage error(String message, Throwable throwable) {
        return LogMessage.builder()
                .messageType(MessageType.ERROR)
                .message(message)
                .throwable(throwable)
                .build();
    }

    static LogMessage error(String message, Object... args) {
        return LogMessage.builder()
                .messageType(MessageType.ERROR)
                .message(message)
                .args(args)
                .build();
    }

    static LogMessage error(String message) {
        return LogMessage.builder()
                .messageType(MessageType.ERROR)
                .message(message)
                .build();
    }

    static LogMessage info(String message, Object... args) {
        return LogMessage.builder()
                .messageType(MessageType.INFO)
                .message(message)
                .args(args)
                .build();
    }

    static LogMessage info(String message) {
        return LogMessage.builder()
                .messageType(MessageType.INFO)
                .message(message)
                .build();
    }

    static LogMessage debug(String message, Object... args) {
        return LogMessage.builder()
                .messageType(MessageType.DEBUG)
                .message(message)
                .args(args)
                .build();
    }

    static LogMessage warn(String message, Object... args) {
        return LogMessage.builder()
                .messageType(MessageType.WARN)
                .message(message)
                .args(args)
                .build();
    }

    Map<String, String> getMdcContext() {
        return mdcContext;
    }

    void print(Logger logger) {
        messageType.action.accept(logger, this);
    }

    @RequiredArgsConstructor
    private enum MessageType {

        INFO((logger, message) -> {
            if (message.args != null && message.args.length > 0) {
                logger.info(message.message, message.args);
            } else {
                logger.info(message.message);
            }
        }),
        ERROR((logger, message) -> {
            if (message.args != null && message.args.length > 0) {
                logger.error(message.message, message.throwable, message.args);
            } else {
                logger.error(message.message, message.throwable);
            }
        }),
        DEBUG((logger, message) -> {
            if (message.args != null && message.args.length > 0) {
                logger.debug(message.message, message.args);
            } else {
                logger.debug(message.message);
            }
        }),
        WARN((logger, message) -> {
            if (message.args != null && message.args.length > 0) {
                logger.warn(message.message, message.args);
            } else {
                logger.warn(message.message);
            }
        });

        private final BiConsumer<Logger, LogMessage> action;
    }
}
