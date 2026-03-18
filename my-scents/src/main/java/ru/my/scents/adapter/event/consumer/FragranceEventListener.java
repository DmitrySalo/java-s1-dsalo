package ru.my.scents.adapter.event.consumer;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.kafka.retrytopic.SameIntervalTopicReuseStrategy;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;
import ru.my.scents.adapter.event.EventHandler;
import ru.my.scents.fragrance.adapter.event.fragrance.proto.FragranceEvent;
import ru.my.scents.infra.logger.Logger;

@Component
@RequiredArgsConstructor
public class FragranceEventListener {

    private final Logger log;
    private final EventHandler<FragranceEvent> eventHandler;

    @RetryableTopic(
            attempts = "${kafka.retry.attempts:3}",
            backoff = @Backoff(
                    delayExpression = "${kafka.retry.initial-interval-ms}",
                    multiplierExpression = "${kafka.retry.multiplier}",
                    maxDelayExpression = "${kafka.retry.max-interval-ms}"
            ),
            dltStrategy = DltStrategy.FAIL_ON_ERROR,
            topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE,
            sameIntervalTopicReuseStrategy = SameIntervalTopicReuseStrategy.SINGLE_TOPIC,
            kafkaTemplate = "fragranceEventKafkaTemplate"
    )
    @KafkaListener(
            topics = "${kafka.topics.outer-fragrance.name}",
            containerFactory = "fragranceEventListenerFactory",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void handleFragranceEvent(
            @Payload FragranceEvent event,
            @Header(KafkaHeaders.RECEIVED_KEY) String key,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset
    ) {
        log.info("Принят FragranceEvent: key={}, topic={}, partition={}, offset={}",
                key, topic, partition, offset);
        eventHandler.handle(event);
        log.info("Успешно обработан FragranceEvent: key={}", key);
    }

    @DltHandler
    public void handleDlt(
            @Payload FragranceEvent event,
            @Header(KafkaHeaders.RECEIVED_KEY) String key,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            @Header(KafkaHeaders.EXCEPTION_MESSAGE) String exceptionMessage
    ) {
        log.info("Принято сообщение в DLT: key={}, topic={}, partition={}, offset={}, exception={}",
                key, topic, partition, offset, exceptionMessage);

        FailedMessage failedMessage = FailedMessage.builder()
                .topic(topic)
                .key(key)
                .payload(event.toByteArray())
                .errorMessage(exceptionMessage)
                .timestamp(event.getEventTimestamp().getSeconds())
                .build();

        log.info("Обработано сообщение из DLT: key={}, topic={}, partition={}, offset={}, message={}",
                key, topic, partition, offset, failedMessage);
    }
}
