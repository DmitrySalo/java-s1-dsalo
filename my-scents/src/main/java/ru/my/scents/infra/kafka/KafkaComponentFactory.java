package ru.my.scents.infra.kafka;

import com.google.protobuf.Message;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.KafkaTemplate;

public interface KafkaComponentFactory {

    <V extends Message> KafkaTemplate<String, V> createProducer(
            Serializer<V> valueSerializer
    );

    <V extends Message> ConcurrentKafkaListenerContainerFactory<String, V> createListenerFactory(
            Deserializer<V> valueDeserializer
    );
}
