package ru.my.scents.infra.kafka;

import com.google.protobuf.Message;
import com.google.protobuf.Parser;
import lombok.AllArgsConstructor;
import org.apache.kafka.common.serialization.Deserializer;

@AllArgsConstructor
public class ProtobufDeserializer<T extends Message> implements Deserializer<T> {

    private final Parser<T> parser;

    @Override
    public T deserialize(String topic, byte[] data) {
        if (data == null || data.length == 0) {
            return null;
        }

        try {
            return parser.parseFrom(data);
        } catch (Exception e) {
            throw new ProtobufDeserializationException("Не удалось десериализовать сообщение protobuf из топика: %s"
                    .formatted(topic), e);
        }
    }

    public static class ProtobufDeserializationException extends RuntimeException {

        public ProtobufDeserializationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}