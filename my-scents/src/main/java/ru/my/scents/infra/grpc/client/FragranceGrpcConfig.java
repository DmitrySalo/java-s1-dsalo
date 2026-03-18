package ru.my.scents.infra.grpc.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.micrometer.core.instrument.MeterRegistry;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceServiceGrpc;
import ru.my.scents.infra.grpc.client.middleware.GrpcMetricsInterceptor;
import ru.my.scents.infra.grpc.client.middleware.GrpcRetryInterceptor;
import ru.my.scents.infra.logger.Logger;

@Configuration
public class FragranceGrpcConfig {

    @Bean(name = "fragranceServiceProperties")
    @ConfigurationProperties(prefix = "grpc.fragrance-service")
    GrpcProperties fragranceServiceProperties() {
        return new GrpcProperties();
    }

    @Bean(destroyMethod = "shutdown", name = "fragranceServiceRetryScheduler")
    ScheduledExecutorService fragranceServiceRetryScheduler(
            @Qualifier("fragranceServiceProperties") GrpcProperties props
    ) {
        ThreadFactory threadFactory = new ThreadFactory() {
            private final AtomicInteger idx = new AtomicInteger();

            @Override
            public Thread newThread(@NotNull Runnable runnable) {
                return new Thread(runnable, "fragrance-grpc-retry-" + idx.getAndIncrement());
            }
        };

        return Executors.newScheduledThreadPool(props.getRetry().getThreadPoolSize(), threadFactory);
    }

    @Bean
    GrpcRetryInterceptor fragranceServiceRetryInterceptor(
            Logger logger,
            @Qualifier("fragranceServiceProperties") GrpcProperties props,
            @Qualifier("fragranceServiceRetryScheduler") ScheduledExecutorService fragranceServiceRetryScheduler
    ) {
        return new GrpcRetryInterceptor(
                logger,
                props.getRetry().getMaxAttempts(),
                props.getRetry().getInitialBackoff(),
                props.getRetry().getMaxBackoff(),
                props.getRetry().getMultiplier(),
                props.getRetry().getPerAttemptTimeout(),
                fragranceServiceRetryScheduler);
    }

    @Bean
    GrpcMetricsInterceptor fragranceServiceMetricsInterceptor(MeterRegistry meterRegistry) {
        return new GrpcMetricsInterceptor(meterRegistry, "fragrance-service");
    }

    @Bean(destroyMethod = "shutdown")
    ManagedChannel fragranceServiceChannel(
            @Qualifier("fragranceServiceProperties") GrpcProperties props,
            GrpcRetryInterceptor fragranceServiceRetryInterceptor,
            GrpcMetricsInterceptor fragranceServiceMetricsInterceptor
    ) {
        return ManagedChannelBuilder
                .forAddress(props.getHost(), props.getPort())
                .usePlaintext()
                .intercept(fragranceServiceMetricsInterceptor)
                .intercept(fragranceServiceRetryInterceptor)
                .build();
    }

    @Bean
    FragranceServiceGrpc.FragranceServiceBlockingV2Stub fragranceServiceStub(
            ManagedChannel fragranceServiceChannel
    ) {
        return FragranceServiceGrpc.newBlockingV2Stub(fragranceServiceChannel);
    }
}
