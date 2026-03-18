package ru.my.scents.boundary.event;

import ru.my.scents.fragrance.adapter.event.fragrance.proto.FragranceEvent;

public interface FragranceEventPublisher {

   void send(FragranceEvent event);
}
