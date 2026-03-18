package ru.my.scents.adapter.event;

import com.google.protobuf.Message;

public interface EventHandler<T extends Message> {

    void handle(T event);
}
