package ru.my.scents.infra.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import ru.my.scents.fragrance.adapter.event.fragrance.proto.FragranceEvent;

@Configuration
@RequiredArgsConstructor
public class FragranceKafkaConfiguration {

    private final KafkaComponentFactory componentFactory;

    @Bean
    public KafkaTemplate<String, FragranceEvent> fragranceEventKafkaTemplate() {
        return componentFactory.createProducer(new ProtobufSerializer<>());
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, FragranceEvent> fragranceEventListenerFactory() {
        return componentFactory.createListenerFactory(new ProtobufDeserializer<>(FragranceEvent.parser()));
    }
}
