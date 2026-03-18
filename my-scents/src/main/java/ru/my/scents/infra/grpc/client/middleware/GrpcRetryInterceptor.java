package ru.my.scents.infra.grpc.client.middleware;

import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.Context;
import io.grpc.Deadline;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import io.grpc.Status;
import java.time.Duration;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import ru.my.scents.infra.logger.Logger;

@RequiredArgsConstructor
public class GrpcRetryInterceptor implements ClientInterceptor {

    private final Logger logger;
    private final int maxAttempts;
    private final Duration initialBackoff;
    private final Duration maxBackoff;
    private final double multiplier;
    private final Duration perAttemptTimeout;
    private final ScheduledExecutorService scheduler;

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(
            MethodDescriptor<ReqT, RespT> method,
            CallOptions callOptions,
            Channel next) {

        if (method.getType() != MethodDescriptor.MethodType.UNARY) {
            return next.newCall(method, callOptions);
        }

        return new RetryingClientCall<>(next, method, callOptions, 1);
    }

    class RetryingClientCall<ReqT, RespT> extends ClientCall<ReqT, RespT> {

        private final Channel channel;
        private final MethodDescriptor<ReqT, RespT> method;
        private final CallOptions baseCallOptions;
        private final int attemptNumber;

        private volatile ClientCall<ReqT, RespT> delegate;
        private volatile Listener<RespT> responseListener;
        private volatile ReqT bufferedMessage;
        private volatile Metadata bufferedHeaders;
        private volatile Context savedContext;
        private volatile Deadline overallDeadline;
        private volatile ScheduledFuture<?> scheduledRetry;

        RetryingClientCall(
                Channel channel,
                MethodDescriptor<ReqT, RespT> method,
                CallOptions baseCallOptions,
                int attemptNumber) {

            this.channel = channel;
            this.method = method;
            this.baseCallOptions = baseCallOptions;
            this.attemptNumber = attemptNumber;
        }

        RetryingClientCall(
                Channel channel,
                MethodDescriptor<ReqT, RespT> method,
                CallOptions baseCallOptions,
                int attemptNumber,
                Context savedContext,
                Deadline overallDeadline) {

            this.channel = channel;
            this.method = method;
            this.baseCallOptions = baseCallOptions;
            this.attemptNumber = attemptNumber;
            this.savedContext = savedContext;
            this.overallDeadline = overallDeadline;
        }

        @Override
        public void start(Listener<RespT> listener, Metadata headers) {
            this.responseListener = listener;
            this.bufferedHeaders = headers;

            if (attemptNumber == 1) {
                this.savedContext = Context.current();
                this.overallDeadline = savedContext.getDeadline();
            }

            CallOptions callOptionsWithDeadline = applyPerAttemptDeadline(baseCallOptions);

            delegate = channel.newCall(method, callOptionsWithDeadline);
            delegate.start(new RetryListener(), headers);
        }

        @Override
        public void sendMessage(ReqT message) {
            bufferedMessage = message;
            delegate.sendMessage(message);
        }

        @Override
        public void halfClose() {
            delegate.halfClose();
        }

        @Override
        public void request(int numMessages) {
            delegate.request(numMessages);
        }

        @Override
        public void cancel(@Nullable String message, @Nullable Throwable cause) {
            ScheduledFuture<?> currentScheduledRetry = scheduledRetry;
            if (currentScheduledRetry != null) {
                currentScheduledRetry.cancel(false);
            }

            ClientCall<ReqT, RespT> currentDelegate = delegate;
            if (currentDelegate != null) {
                currentDelegate.cancel(message, cause);
            }
        }

        private CallOptions applyPerAttemptDeadline(CallOptions options) {
            if (overallDeadline == null) {
                return options.withDeadlineAfter(perAttemptTimeout);
            }

            long remainingNanos = overallDeadline.timeRemaining(TimeUnit.NANOSECONDS);
            long perAttemptNanos = perAttemptTimeout.getNano();

            long effectiveNanos = Math.min(remainingNanos, perAttemptNanos);

            return options.withDeadline(Deadline.after(effectiveNanos, TimeUnit.NANOSECONDS));
        }

        class RetryListener extends Listener<RespT> {
            @Override
            public void onClose(Status status, Metadata trailers) {
                if (status.isOk()) {
                    responseListener.onClose(status, trailers);
                    return;
                }

                if (shouldRetry(status, attemptNumber)) {
                    scheduleRetry(status, trailers);
                    return;
                }

                responseListener.onClose(status, trailers);
            }

            @Override
            public void onMessage(RespT message) {
                responseListener.onMessage(message);
            }

            @Override
            public void onHeaders(Metadata headers) {
                responseListener.onHeaders(headers);
            }

            @Override
            public void onReady() {
                responseListener.onReady();
            }
        }

        private boolean shouldRetry(Status status, int attempt) {
            if (attempt >= maxAttempts) {
                logger.info("Достигнуто максимальное количество попыток {} для {}",
                        maxAttempts, method.getFullMethodName());
                return false;
            }

            if (!isRetriable(status)) {
                return false;
            }

            if (overallDeadline != null && overallDeadline.isExpired()) {
                logger.info("Общий срок истек, повторная попытка невозможна для {}",
                        method.getFullMethodName());
                return false;
            }

            return true;
        }

        private void scheduleRetry(Status status, Metadata trailers) {
            int attemptIndex = attemptNumber - 1;
            long backoffMs = calculateBackoff(attemptIndex);

            logger.info("Повторная попытка {}/{} вызова {} после {}ms (код: {})",
                    attemptNumber + 1, maxAttempts, method.getFullMethodName(), backoffMs, status.getCode());

            Runnable retryTask = () -> savedContext.run(() -> retryCall(status, trailers));

            scheduledRetry = scheduler.schedule(
                    () -> retryTask,
                    backoffMs,
                    TimeUnit.MILLISECONDS
            );
        }

        private void retryCall(Status status, Metadata trailers) {
            try {
                var retryCall = new RetryingClientCall<>(
                        channel, method, baseCallOptions, attemptNumber + 1,
                        savedContext, overallDeadline);

                retryCall.start(responseListener, bufferedHeaders);
                retryCall.request(1);

                if (bufferedMessage != null) {
                    retryCall.sendMessage(bufferedMessage);
                }

                retryCall.halfClose();
            } catch (Exception e) {
                logger.error("Ошибка при попытке повторного вызова {}: {}",
                        e, method.getFullMethodName(), e.getMessage());

                responseListener.onClose(status, trailers);
            }
        }

        private boolean isRetriable(Status status) {
            return status.getCode() == Status.Code.UNAVAILABLE
                    || status.getCode() == Status.Code.DEADLINE_EXCEEDED
                    || status.getCode() == Status.Code.RESOURCE_EXHAUSTED;
        }

        private long calculateBackoff(int attemptIndex) {
            long baseBackoff = (long) (initialBackoff.toMillis() * Math.pow(multiplier, attemptIndex));
            long cappedBackoff = Math.min(baseBackoff, maxBackoff.toMillis());

            return ThreadLocalRandom.current().nextLong(0, cappedBackoff + 1);
        }
    }
}
