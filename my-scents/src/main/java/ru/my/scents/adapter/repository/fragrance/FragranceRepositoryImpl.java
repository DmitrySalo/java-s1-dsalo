package ru.my.scents.adapter.repository.fragrance;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;
import ru.my.scents.adapter.repository.fragrance.converter.FragranceConverter;
import ru.my.scents.adapter.repository.fragrance.model.FragranceDBModel;
import ru.my.scents.boundary.repository.FragranceRepository;
import ru.my.scents.domain.entity.fragrance.Fragrance;
import ru.my.scents.domain.entity.fragrance.FragranceID;

@Repository
@RequiredArgsConstructor
public class FragranceRepositoryImpl implements FragranceRepository {

    private final MongoTemplate mongoTemplate;

    @Override
    public Optional<Fragrance> findById(final FragranceID fragranceID) {
        FragranceDBModel dbModel = mongoTemplate.findById(fragranceID.getValue(), FragranceDBModel.class);
        return Optional.ofNullable(dbModel).map(FragranceConverter::toEntity);
    }

    @Override
    public void save(final Fragrance fragrance) {
        FragranceDBModel model = FragranceConverter.toDbModel(fragrance);
        mongoTemplate.save(model);
    }

    @Override
    public void delete(final FragranceID fragranceID) {
        FragranceDBModel dbModel = mongoTemplate.findById(fragranceID.getValue(), FragranceDBModel.class);
        Optional.ofNullable(dbModel)
                .map(mongoTemplate::remove)
                .orElseThrow(() -> new IllegalStateException("Парфюма c ID {%s} не существует!"
                        .formatted(fragranceID.getValue())));
    }
}
