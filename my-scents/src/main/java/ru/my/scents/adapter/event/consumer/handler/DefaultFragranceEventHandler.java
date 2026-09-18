package ru.my.scents.adapter.event.consumer.handler;

import java.util.Map;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.my.scents.adapter.event.EventHandler;
import ru.my.scents.adapter.event.consumer.converter.CreateFragranceParamMapper;
import ru.my.scents.adapter.event.consumer.converter.UpdateFragranceParamMapper;
import ru.my.scents.boundary.model.fragrance.CreateFragranceParam;
import ru.my.scents.boundary.model.fragrance.UpdateFragranceParam;
import ru.my.scents.boundary.usecase.FragranceUseCase;
import ru.my.scents.fragrance.adapter.event.fragrance.proto.FragranceEvent;
import ru.my.scents.fragrance.adapter.event.fragrance.proto.FragranceEventType;
import static ru.my.scents.fragrance.adapter.event.fragrance.proto.FragranceEventType.FRAGRANCE_EVENT_TYPE_CREATED;
import static ru.my.scents.fragrance.adapter.event.fragrance.proto.FragranceEventType.FRAGRANCE_EVENT_TYPE_DELETED;
import static ru.my.scents.fragrance.adapter.event.fragrance.proto.FragranceEventType.FRAGRANCE_EVENT_TYPE_UPDATED;
import ru.my.scents.infra.logger.Logger;

@Service
@RequiredArgsConstructor
class DefaultFragranceEventHandler implements EventHandler<FragranceEvent> {

    private final Logger log;
    private final FragranceUseCase fragranceUseCase;
    private final Map<FragranceEventType, Supplier<Command>> executionStrategy = Map.of(
            FRAGRANCE_EVENT_TYPE_CREATED, this::create,
            FRAGRANCE_EVENT_TYPE_UPDATED, this::update,
            FRAGRANCE_EVENT_TYPE_DELETED, this::delete
    );

    @Override
    public void handle(FragranceEvent event) {
        log.debug("Обрабатываю FragranceEvent: ID={}, Type={}", event.getEventId(), event.getEventType());
        executionStrategy.get(event.getEventType()).get().execute(event);
        log.debug("Обработан FragranceEvent: ID={}, Type={}", event.getEventId(), event.getEventType());
    }

    private Command create() {
        return event -> {
            CreateFragranceParam param = CreateFragranceParamMapper.toParam(event);
            fragranceUseCase.create(param);
        };
    }

    private Command update() {
        return event -> {
            UpdateFragranceParam param = UpdateFragranceParamMapper.toParam(event);
            fragranceUseCase.update(param);
        };
    }

    private Command delete() {
        return event -> fragranceUseCase.delete(event.getPayload().getId());
    }

    @FunctionalInterface
    private interface Command {

        void execute(FragranceEvent event);
    }
}
