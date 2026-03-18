package ru.my.scents.infra.kafka;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;

@EnableKafka
@Configuration
public class MainKafkaConfiguration {

    @Bean("outerFragranceTopicProperties")
    @ConfigurationProperties(prefix = "kafka.topics.outer-fragrance")
    public TopicProperties outerFragranceTopicProperties() {
        return new TopicProperties();
    }

    @Bean("outerFragranceDltTopicProperties")
    @ConfigurationProperties(prefix = "kafka.topics.outer-fragrance-dlt")
    public TopicProperties outerFragranceDltTopicProperties() {
        return new TopicProperties();
    }

    @Bean("innerFragranceTopicProperties")
    @ConfigurationProperties(prefix = "kafka.topics.my-scents-fragrance")
    public TopicProperties innerFragranceTopicProperties() {
        return new TopicProperties();
    }

    @Bean("innerFragranceDltTopicProperties")
    @ConfigurationProperties(prefix = "kafka.topics.my-scents-fragrance-dlt")
    public TopicProperties innerFragranceDltTopicProperties() {
        return new TopicProperties();
    }
}