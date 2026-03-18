package ru.my.scents.adapter.event.consumer;

import java.time.Instant;
import java.util.Arrays;
import lombok.Builder;
import org.jetbrains.annotations.NotNull;

@Builder
public record FailedMessage(String topic,
                            String key,
                            int partition,
                            long offset,
                            byte[] payload,
                            String errorMessage,
                            long timestamp,
                            Instant createdAt) {

    @NotNull
    @Override
    public String toString() {
        return "FailedMessage{" +
                "topic='" + topic + '\'' +
                ", key='" + key + '\'' +
                ", partition=" + partition +
                ", offset=" + offset +
                ", payload=" + Arrays.toString(payload) +
                ", errorMessage='" + errorMessage + '\'' +
                ", timestamp=" + timestamp +
                ", createdAt=" + createdAt +
                '}';
    }
}