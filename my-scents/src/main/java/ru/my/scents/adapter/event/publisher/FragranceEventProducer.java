package ru.my.scents.adapter.event.publisher;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.my.scents.adapter.event.AbstractProtobufProducer;
import ru.my.scents.boundary.event.FragranceEventPublisher;
import ru.my.scents.fragrance.adapter.event.fragrance.proto.FragranceEvent;
import ru.my.scents.infra.kafka.TopicProperties;
import ru.my.scents.infra.logger.Logger;

@Component
public class FragranceEventProducer extends AbstractProtobufProducer<FragranceEvent> implements FragranceEventPublisher {

    private final Logger log;
    private final String topicName;

    FragranceEventProducer(
            Logger log,
            KafkaTemplate<String, FragranceEvent> kafkaTemplate,
            @Qualifier("innerFragranceTopicProperties") TopicProperties topicProperties
    ) {
        super(log, kafkaTemplate);
        this.log = log;
        this.topicName = topicProperties.getName();
    }

    @Override
    public void send(FragranceEvent event) {
        send(topicName, event.getPayload().getId(), event).whenComplete((res, err) -> {
            if (err == null) {
                log.info("Отправлен FragranceEvent: fragranceId {}, event {}",
                        event.getPayload().getId(), event);
                return;
            }

            log.error(err.getMessage(), err);
            throw new RuntimeException(err);
        });
    }

    public CompletableFuture<Void> sendFragranceEvent(
            String fragranceId,
            FragranceEvent event,
            Map<String, String> headers
    ) {
        return send(topicName, fragranceId, event, headers);
    }
}
