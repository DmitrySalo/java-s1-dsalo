package ru.my.scents.infra.grpc.client;

import java.time.Duration;
import java.util.Random;
import lombok.Data;

@Data
public class GrpcProperties {

    private String host = "localhost";
    private int port = new Random().nextInt(10000, 90000);
    private Duration deadline = Duration.ofSeconds(30);
    private RetryProperties retry;

    @Data
    public static class RetryProperties {

        private int maxAttempts = 1;
        private Duration initialBackoff = Duration.ofMillis(500);
        private Duration maxBackoff = Duration.ofMillis(1000);
        private double multiplier = 1.0;
        private Duration perAttemptTimeout = Duration.ofMillis(500);
        private int threadPoolSize = Runtime.getRuntime().availableProcessors();
    }
}
