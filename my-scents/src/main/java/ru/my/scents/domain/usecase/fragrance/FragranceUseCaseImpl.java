package ru.my.scents.domain.usecase.fragrance;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.my.scents.boundary.event.FragranceEventPublisher;
import ru.my.scents.boundary.gateway.FragranceGateway;
import ru.my.scents.boundary.model.fragrance.CreateFragranceParam;
import ru.my.scents.boundary.model.fragrance.UpdateFragranceParam;
import ru.my.scents.boundary.repository.FragranceRepository;
import ru.my.scents.boundary.usecase.FragranceUseCase;
import ru.my.scents.domain.entity.fragrance.Fragrance;
import ru.my.scents.domain.entity.fragrance.FragranceID;
import ru.my.scents.domain.usecase.fragrance.converter.FragranceEventConverter;
import ru.my.scents.domain.usecase.fragrance.converter.FragranceConverter;
import ru.my.scents.fragrance.adapter.event.fragrance.proto.FragranceEvent;
import ru.my.scents.infra.logger.Logger;

@Service
@RequiredArgsConstructor
public class FragranceUseCaseImpl implements FragranceUseCase {

    private final Logger logger;
    private final FragranceEventPublisher publisher;
    private final FragranceGateway fragranceGateway;
    private final FragranceRepository fragranceRepository;

    @Override
    public Fragrance create(CreateFragranceParam params) {
        Fragrance fragrance = FragranceConverter.toEntity(params);
        fragranceRepository.save(fragrance);

        Fragrance saved = get(fragrance.getId().toString());
        FragranceEvent event = FragranceEventConverter.toCreateEvent(saved);
        publisher.send(event);

        logger.info("Парфюм создан с ID {}", fragrance.getId());

        return fragrance;
    }

    @Override
    public Fragrance update(UpdateFragranceParam params) {
        Fragrance fragrance = FragranceConverter.toEntity(params);
        fragranceRepository.save(fragrance);

        Fragrance updated = get(fragrance.getId().toString());
        FragranceEvent event = FragranceEventConverter.toUpdateEvent(updated);
        publisher.send(event);

        logger.info("Парфюм обновлён с ID {}", fragrance.getId());

        return fragrance;
    }

    @Override
    public Fragrance get(String fragranceId) {
        FragranceID id = FragranceID.of(fragranceId);

        Optional<Fragrance> fragranceOpt = fragranceRepository.findById(id);

        if (fragranceOpt.isPresent()) {
            return fragranceOpt.get();
        }

        fragranceOpt = fragranceGateway.findById(id);

        if (fragranceOpt.isPresent()) {
            Fragrance fragrance = fragranceOpt.get();
            fragranceRepository.save(fragrance);
            logger.info("Парфюм создан с ID {}", fragrance.getId());
            return fragrance;
        }

        throw new NullPointerException(fragranceId);
    }

    @Override
    public void delete(String fragranceId) {
        FragranceID id = FragranceID.of(fragranceId);
        fragranceRepository.delete(id);

        FragranceEvent event = FragranceEventConverter.toDeleteEvent(fragranceId);
        publisher.send(event);

        logger.info("Парфюм с ID {} удалён", id.getValue());
    }
}
