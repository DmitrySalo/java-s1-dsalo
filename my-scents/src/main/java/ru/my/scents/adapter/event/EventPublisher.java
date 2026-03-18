package ru.my.scents.adapter.event;

import com.google.protobuf.Message;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface EventPublisher<V extends Message> {

    CompletableFuture<Void> send(String topic, String key, V message);

    CompletableFuture<Void> send(String topic, String key, V message, Map<String, String> headers);
}
