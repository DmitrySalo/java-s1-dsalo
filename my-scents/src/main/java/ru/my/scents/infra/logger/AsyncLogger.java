package ru.my.scents.infra.logger;

import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.SmartLifecycle;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Primary
@Component
public class AsyncLogger implements Logger, SmartLifecycle {

    private final List<Logger> loggers;
    private final BlockingQueue<LogMessage> logQueue;
    private final ExecutorService executor;

    private final AtomicBoolean running = new AtomicBoolean(false);

    public AsyncLogger(List<Logger> loggers, @Value("${logging.queue.capacity:100}") int queueCapacity) {

        this.loggers = loggers.stream()
                .filter(logger -> !(logger instanceof AsyncLogger))
                .toList();

        this.logQueue = new LinkedBlockingQueue<>(queueCapacity);
        this.executor = Executors.newSingleThreadExecutor();
    }

    private void processMessages() {
        while (running.get()) {
            try {
                var message = logQueue.take();
                handleLogMessage(message);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } finally {
                MDC.clear();
            }
        }

        drainRemainingMessages();
    }

    private void drainRemainingMessages() {
        while (true) {
            var message = logQueue.poll();
            if (message == null) {
                break;
            }

            try {
                handleLogMessage(message);
            } finally {
                MDC.clear();
            }
        }
    }

    private void handleLogMessage(final LogMessage message) {
        if (message.getMdcContext() != null) {
            MDC.setContextMap(message.getMdcContext());
        }

        for (var logger : loggers) {
            switch (message.messageType) {
                case INFO:
                    if (message.args != null && message.args.length > 0) {
                        logger.info(message.message, message.args);
                    } else {
                        logger.info(message.message);
                    }
                    break;
                case ERROR:
                    if (message.args != null && message.args.length > 0) {
                        logger.error(message.message, message.throwable, message.args);
                    } else {
                        logger.error(message.message, message.throwable);
                    }
                    break;
                case DEBUG:
                    if (message.args != null && message.args.length > 0) {
                        logger.debug(message.message, message.args);
                    }
                    break;
                case WARN:
                    if (message.args != null && message.args.length > 0) {
                        logger.warn(message.message, message.args);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void error(String message, Throwable throwable) {
        if (!running.get()) {
            loggers.forEach(logger -> logger.error(message, throwable));
            return;
        }

        try {
            logQueue.put(new LogMessage(LogMessage.MessageType.ERROR, message, null, throwable));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            loggers.forEach(logger -> logger.error(message, throwable));
        }
    }

    @Override
    public void error(String message) {
        if (!running.get()) {
            loggers.forEach(logger -> logger.error(message));
            return;
        }

        try {
            logQueue.put(new LogMessage(LogMessage.MessageType.ERROR, message, null, null));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            loggers.forEach(logger -> logger.error(message));
        }
    }

    @Override
    public void info(String message) {
        if (!running.get()) {
            loggers.forEach(logger -> logger.info(message));
            return;
        }

        try {
            logQueue.put(new LogMessage(LogMessage.MessageType.INFO, message, null, null));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            loggers.forEach(logger -> logger.info(message));
        }
    }

    @Override
    public void error(String message, Throwable throwable, Object... args) {
        if (!running.get()) {
            loggers.forEach(logger -> logger.error(message, throwable, args));
            return;
        }

        try {
            logQueue.put(new LogMessage(LogMessage.MessageType.ERROR, message, args, throwable));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            loggers.forEach(logger -> logger.error(message, throwable, args));
        }
    }

    @Override
    public void error(String message, Object... args) {
        if (!running.get()) {
            loggers.forEach(logger -> logger.error(message, args));
            return;
        }

        try {
            logQueue.put(new LogMessage(LogMessage.MessageType.ERROR, message, args, null));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            loggers.forEach(logger -> logger.error(message, args));
        }
    }

    @Override
    public void info(String message, Object... args) {
        if (!running.get()) {
            loggers.forEach(logger -> logger.info(message, args));
            return;
        }

        try {
            logQueue.put(new LogMessage(LogMessage.MessageType.INFO, message, args, null));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            loggers.forEach(logger -> logger.info(message, args));
        }
    }

    @Override
    public void debug(String message, Object... args) {
        if (!running.get()) {
            loggers.forEach(logger -> logger.debug(message, args));
            return;
        }

        try {
            logQueue.put(new LogMessage(LogMessage.MessageType.DEBUG, message, args, null));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            loggers.forEach(logger -> logger.debug(message, args));
        }
    }

    @Override
    public void warn(String message, Object... args) {
        if (!running.get()) {
            loggers.forEach(logger -> logger.warn(message, args));
            return;
        }

        try {
            logQueue.put(new LogMessage(LogMessage.MessageType.WARN, message, args, null));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            loggers.forEach(logger -> logger.warn(message, args));
        }
    }

    @Override
    public void start() {
        if (running.compareAndSet(false, true)) {
            executor.submit(this::processMessages);
        }
    }

    @Override
    public void stop() {
        running.compareAndSet(true, false);
        executor.shutdown();
        try {
            if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public boolean isRunning() {
        return running.get();
    }

    private static class LogMessage {

        private final MessageType messageType;
        private final String message;
        private final Throwable throwable;
        private final Object[] args;
        private final Map<String, String> mdcContext;

        private LogMessage(MessageType messageType, String message, Object[] args, Throwable throwable) {
            this.messageType = messageType;
            this.message = message;
            this.throwable = throwable;
            this.args = args;
            this.mdcContext = MDC.getCopyOfContextMap();
        }

        Map<String, String> getMdcContext() {
            return mdcContext;
        }

        private enum MessageType {

            INFO,
            ERROR,
            DEBUG,
            WARN
        }
    }
}
