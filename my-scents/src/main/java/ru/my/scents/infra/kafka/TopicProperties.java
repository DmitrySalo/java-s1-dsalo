package ru.my.scents.infra.kafka;

import java.util.UUID;
import lombok.Data;

@Data
public class TopicProperties {

    private String name = "test-topic-" + UUID.randomUUID();
    private boolean autoCreateTopics = false;
    private int concurrency = 1;
    private int replicationFactor = 1;
    private int numPartitions = 1;
}
