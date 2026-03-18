package ru.my.scents.infra.kafka;

import com.google.protobuf.Message;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.stereotype.Component;
import ru.my.scents.infra.logger.Logger;

@Component
@RequiredArgsConstructor
public class DefaultKafkaComponentFactory implements KafkaComponentFactory {

    private final Logger log;
    private final KafkaProperties kafkaProperties;

    @Override
    public <V extends Message> KafkaTemplate<String, V> createProducer(
            Serializer<V> valueSerializer
    ) {
        ProducerFactory<String, V> producerFactory = createProducerFactory(valueSerializer);
        return new KafkaTemplate<>(producerFactory);
    }

    private <V extends Message> ProducerFactory<String, V> createProducerFactory(
            Serializer<V> valueSerializer
    ) {
        return new DefaultKafkaProducerFactory<>(
                buildProducerProperties(),
                new StringSerializer(),
                valueSerializer
        );
    }

    private Map<String, Object> buildProducerProperties() {
        Map<String, Object> props = new HashMap<>(kafkaProperties.buildProducerProperties());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return props;
    }

    @Override
    public <V extends Message> ConcurrentKafkaListenerContainerFactory<String, V> createListenerFactory(
            Deserializer<V> valueDeserializer
    ) {
        ConsumerFactory<String, V> consumerFactory = createConsumerFactory(valueDeserializer);

        var factory = new ConcurrentKafkaListenerContainerFactory<String, V>();
        factory.setConsumerFactory(consumerFactory);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);

        return factory;
    }

    private <V extends Message> ConsumerFactory<String, V> createConsumerFactory(
            Deserializer<V> valueDeserializer
    ) {
        var keyDeserializer = new ErrorHandlingDeserializer<>(new StringDeserializer());
        keyDeserializer.setFailedDeserializationFunction(data -> {
            log.error("Ошибка десериализации ключа", data.getException().getMessage());
            throw new RuntimeException(data.getException());
        });

        var dataDeserializer = new ErrorHandlingDeserializer<>(valueDeserializer);
        dataDeserializer.setFailedDeserializationFunction(data -> {
            log.error("Ошибка десериализации значения", data.getException().getMessage());
            throw new RuntimeException(data.getException());
        });

        return new DefaultKafkaConsumerFactory<>(
                buildConsumerProperties(),
                keyDeserializer,
                dataDeserializer
        );
    }

    private Map<String, Object> buildConsumerProperties() {
        Map<String, Object> props = new HashMap<>(kafkaProperties.buildConsumerProperties());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return props;
    }
}