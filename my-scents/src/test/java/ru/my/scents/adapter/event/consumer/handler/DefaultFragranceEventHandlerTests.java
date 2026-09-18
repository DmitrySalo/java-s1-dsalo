package ru.my.scents.adapter.event.consumer.handler;

import static org.mockito.Mockito.verify;

import com.google.protobuf.Timestamp;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.my.scents.adapter.event.EventHandler;
import ru.my.scents.boundary.usecase.FragranceUseCase;
import ru.my.scents.fragrance.adapter.event.fragrance.proto.FragranceEvent;
import ru.my.scents.fragrance.adapter.event.fragrance.proto.FragranceEventPayload;
import ru.my.scents.fragrance.adapter.event.fragrance.proto.FragranceEventType;
import ru.my.scents.infra.logger.Logger;

@ExtendWith(MockitoExtension.class)
class DefaultFragranceEventHandlerTests {

    private static final String EVENT_ID = "event-id";
    private static final String FRAGRANCE_ID = "550e8400-e29b-41d4-a716-446655440000";

    @Mock
    private Logger logger;

    @Mock
    private FragranceUseCase fragranceUseCase;

    @InjectMocks
    private DefaultFragranceEventHandler eventHandler;

    @Test
    @DisplayName("Удаление из Kafka-события использует идентификатор парфюма из payload")
    void shouldDeleteFragranceByPayloadIdWhenDeleteEventReceived() {
        // Arrange
        FragranceEvent event = FragranceEvent.newBuilder()
                .setEventId(EVENT_ID)
                .setEventType(FragranceEventType.FRAGRANCE_EVENT_TYPE_DELETED)
                .setEventTimestamp(Timestamp.getDefaultInstance())
                .setPayload(FragranceEventPayload.newBuilder().setId(FRAGRANCE_ID))
                .build();

        // Act
        eventHandler.handle(event);

        // Assert
        verify(fragranceUseCase).delete(FRAGRANCE_ID);
    }
}
