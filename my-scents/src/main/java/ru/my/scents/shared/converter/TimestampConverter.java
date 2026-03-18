package ru.my.scents.shared.converter;

import com.google.protobuf.Timestamp;
import java.time.Instant;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TimestampConverter {

    public static Timestamp toProto(Instant instant) {
        if (instant == null) {
            return Timestamp.getDefaultInstance();
        }

        return Timestamp.newBuilder()
                .setSeconds(instant.getEpochSecond())
                .setNanos(instant.getNano())
                .build();
    }

    public static Instant toInstant(Timestamp timestamp) {
        if (timestamp == null || timestamp.equals(Timestamp.getDefaultInstance())) {
            return null;
        }

        return Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos());
    }
}
