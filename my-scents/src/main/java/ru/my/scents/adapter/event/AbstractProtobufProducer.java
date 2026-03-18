package ru.my.scents.adapter.event;

import com.google.protobuf.Message;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import ru.my.scents.infra.logger.Logger;

@RequiredArgsConstructor
public abstract class AbstractProtobufProducer<V extends Message> implements EventPublisher<V> {

    private final Logger log;
    private final KafkaTemplate<String, V> kafkaTemplate;

    @Override
    public CompletableFuture<Void> send(String topic, String key, V message) {
        return send(topic, key, message, Map.of());
    }

    @Override
    public CompletableFuture<Void> send(
            String topic,
            String key,
            V message,
            Map<String, String> headers
    ) {
        ProducerRecord<String, V> record = createRecord(topic, key, message, headers);

        return kafkaTemplate.send(record)
                .thenApply(this::logSuccess)
                .exceptionally(this::handleError);
    }

    private ProducerRecord<String, V> createRecord(
            String topic,
            String key,
            V message,
            Map<String, String> headers
    ) {
        ProducerRecord<String, V> record = new ProducerRecord<>(topic, key, message);

        headers.forEach((headerKey, headerValue) ->
                record.headers().add(new RecordHeader(
                        headerKey,
                        headerValue.getBytes(StandardCharsets.UTF_8)
                ))
        );

        return record;
    }

    private Void logSuccess(SendResult<String, V> result) {
        log.info("Сообщение успешно отправлено в topic: {}, partition: {}, offset: {}",
                result.getRecordMetadata().topic(),
                result.getRecordMetadata().partition(),
                result.getRecordMetadata().offset());

        return null;
    }

    private Void handleError(Throwable ex) {
        log.error("Ошибка при отправке сообщения в Kafka: {}", ex.getMessage(), ex);
        throw new MessageSendException("Ошибка при отправке сообщения в Kafka", ex);
    }

    protected static class MessageSendException extends RuntimeException {

        public MessageSendException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
