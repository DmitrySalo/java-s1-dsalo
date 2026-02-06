package ru.my.scents.domain.usecase.fragrance;

import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.my.scents.boundary.gateway.FragranceGateway;
import ru.my.scents.boundary.model.fragrance.CreateFragranceParam;
import ru.my.scents.boundary.model.fragrance.UpdateFragranceParam;
import ru.my.scents.boundary.repository.FragranceRepository;
import ru.my.scents.boundary.usecase.FragranceUseCase;
import ru.my.scents.domain.entity.fragrance.Fragrance;
import ru.my.scents.domain.entity.fragrance.FragranceAvailabilityStatus;
import ru.my.scents.domain.entity.fragrance.FragranceGender;
import ru.my.scents.domain.entity.fragrance.FragranceID;
import ru.my.scents.domain.entity.fragrance.FragranceLongevity;
import ru.my.scents.domain.entity.fragrance.FragranceName;
import ru.my.scents.domain.entity.fragrance.FragranceRating;
import ru.my.scents.domain.entity.fragrance.FragranceResume;
import ru.my.scents.domain.entity.fragrance.FragranceSeason;
import ru.my.scents.domain.entity.fragrance.FragranceSillage;
import ru.my.scents.domain.entity.fragrance.FragranceType;
import ru.my.scents.infra.logger.Logger;

@Service
@RequiredArgsConstructor
public class FragranceUseCaseImpl implements FragranceUseCase {

    private final Logger logger;
    private final FragranceGateway fragranceGateway;
    private final FragranceRepository fragranceRepository;

    @Override
    public Fragrance create(CreateFragranceParam params) {

        Fragrance fragrance = Fragrance.builder()
                .id(FragranceID.of(params.id()))
                .name(FragranceName.of(params.name()))
                .rating(FragranceRating.of(params.rating()))
                .resume(FragranceResume.of(params.resume()))
                .type(params.type().stream().map(FragranceType::valueOf).collect(Collectors.toSet()))
                .season(params.season().stream().map(FragranceSeason::valueOf).collect(Collectors.toSet()))
                .sillage(FragranceSillage.valueOf(params.sillage()))
                .availability(FragranceAvailabilityStatus.valueOf(params.availability()))
                .gender(FragranceGender.valueOf(params.gender()))
                .longevity(FragranceLongevity.valueOf(params.longevity()))
                .createdAt(params.createdAt())
                .build();

        fragranceRepository.save(fragrance);
        logger.info("Парфюм создан с ID {}", fragrance.getId());

        return fragrance;
    }

    @Override
    public Fragrance update(UpdateFragranceParam params) {
        Fragrance fragrance = this.get(params.id());

        fragrance = Fragrance.builder()
                .id(fragrance.getId())
                .name(FragranceName.of(params.name()))
                .rating(FragranceRating.of(params.rating()))
                .resume(FragranceResume.of(params.resume()))
                .type(params.type().stream().map(FragranceType::valueOf).collect(Collectors.toSet()))
                .season(params.season().stream().map(FragranceSeason::valueOf).collect(Collectors.toSet()))
                .sillage(FragranceSillage.valueOf(params.sillage()))
                .availability(FragranceAvailabilityStatus.valueOf(params.availability()))
                .gender(FragranceGender.valueOf(params.gender()))
                .longevity(FragranceLongevity.valueOf(params.longevity()))
                .createdAt(fragrance.getCreatedAt())
                .updatedAt(params.updatedAt())
                .build();

        fragranceRepository.save(fragrance);
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
}
