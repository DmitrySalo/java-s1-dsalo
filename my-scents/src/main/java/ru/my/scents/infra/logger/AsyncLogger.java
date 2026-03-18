package ru.my.scents.infra.logger;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Supplier;
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
                var message = logQueue.poll(100, TimeUnit.MILLISECONDS);
                if (message == null) {
                    continue;
                }

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

        loggers.forEach(message::print);
    }

    @Override
    public void error(String message, Throwable throwable) {
        executeLogAction(
                logger -> logger.error(message, throwable),
                () -> LogMessage.error(message, throwable)
        );
    }

    @Override
    public void error(String message) {
        executeLogAction(
                logger -> logger.error(message),
                () -> LogMessage.error(message)
        );
    }

    @Override
    public void info(String message) {
        executeLogAction(
                logger -> logger.info(message),
                () -> LogMessage.info(message)
        );
    }

    @Override
    public void error(String message, Throwable throwable, Object... args) {
        executeLogAction(
                logger -> logger.error(message, throwable, args),
                () -> LogMessage.error(message, throwable, args)
        );
    }

    @Override
    public void error(String message, Object... args) {
        executeLogAction(
                logger -> logger.error(message, args),
                () -> LogMessage.error(message, args)
        );
    }

    @Override
    public void info(String message, Object... args) {
        executeLogAction(
                logger -> logger.info(message, args),
                () -> LogMessage.info(message, args)
        );
    }

    @Override
    public void debug(String message, Object... args) {
        executeLogAction(
                logger -> logger.debug(message, args),
                () -> LogMessage.debug(message, args)
        );
    }

    @Override
    public void warn(String message, Object... args) {
        executeLogAction(
                logger -> logger.warn(message, args),
                () -> LogMessage.warn(message, args)
        );
    }

    private void executeLogAction(Consumer<Logger> loggerConsumer, Supplier<LogMessage> logMessageSupplier) {
        if (!running.get()) {
            loggers.forEach(loggerConsumer);
            return;
        }

        try {
            logQueue.put(logMessageSupplier.get());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            loggers.forEach(loggerConsumer);
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
}
