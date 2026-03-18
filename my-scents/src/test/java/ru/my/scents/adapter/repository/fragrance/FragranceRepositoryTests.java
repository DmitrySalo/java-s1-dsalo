package ru.my.scents.adapter.repository.fragrance;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertWith;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import ru.my.scents.adapter.repository.fragrance.model.FragranceDBModel;
import ru.my.scents.boundary.repository.FragranceRepository;
import ru.my.scents.configuration.BaseIntegrationTest;
import ru.my.scents.domain.entity.fragrance.Fragrance;
import ru.my.scents.domain.entity.fragrance.FragranceAvailabilityStatus;
import ru.my.scents.domain.entity.fragrance.FragranceConcentration;
import ru.my.scents.domain.entity.fragrance.FragranceGender;
import ru.my.scents.domain.entity.fragrance.FragranceID;
import ru.my.scents.domain.entity.fragrance.FragranceLongevity;
import ru.my.scents.domain.entity.fragrance.FragranceName;
import ru.my.scents.domain.entity.fragrance.FragranceRating;
import ru.my.scents.domain.entity.fragrance.FragranceResume;
import ru.my.scents.domain.entity.fragrance.FragranceSeason;
import ru.my.scents.domain.entity.fragrance.FragranceSillage;
import ru.my.scents.domain.entity.fragrance.FragranceType;
import ru.my.scents.stub.entity.FragranceStub;

@DisplayName(value = "Интеграционные тесты FragranceRepository")
public class FragranceRepositoryTests extends BaseIntegrationTest {

    private final MongoTemplate mongoTemplate;
    private final FragranceRepository fragranceRepository;

    @Autowired
    FragranceRepositoryTests(MongoTemplate mongoTemplate, FragranceRepository fragranceRepository) {
        this.mongoTemplate = mongoTemplate;
        this.fragranceRepository = fragranceRepository;
    }

    @AfterEach
    void cleanUp() {
        mongoTemplate.getCollectionNames().forEach(mongoTemplate::dropCollection);
    }

    @Test
    @DisplayName(value = "Успешно сохраняем новый Fragrance")
    public void successfullySaveNewFragranceTest() {
        // Arrange
        Fragrance toCreate = FragranceStub.createValidFragrance();
        FragranceID fragranceID = toCreate.getId();

        // Act
        fragranceRepository.save(toCreate);

        // Assert
        assertWith(fragranceRepository.findById(fragranceID), optional -> {
                    Fragrance exist = assertThat(optional).isPresent().get().actual();
                    long existFragranceCount = getCount(exist.getId().getValue().toString());

                    assertAll(
                            () -> assertThat(existFragranceCount).isOne(),
                            () -> assertThat(exist.getId()).isEqualTo(fragranceID),
                            () -> assertThat(exist.getName()).isEqualTo(toCreate.getName()),
                            () -> assertThat(exist.getResume()).isEqualTo(toCreate.getResume()),
                            () -> assertThat(exist.getConcentration()).isEqualTo(toCreate.getConcentration()),
                            () -> assertThat(exist.getType()).containsAll(toCreate.getType()),
                            () -> assertThat(exist.getGender()).isEqualTo(toCreate.getGender()),
                            () -> assertThat(exist.getSeason()).containsAll(toCreate.getSeason()),
                            () -> assertThat(exist.getRating()).isEqualTo(toCreate.getRating()),
                            () -> assertThat(exist.getAvailability()).isEqualTo(toCreate.getAvailability()),
                            () -> assertThat(exist.getSillage()).isEqualTo(toCreate.getSillage()),
                            () -> assertThat(exist.getLongevity()).isEqualTo(toCreate.getLongevity()),
                            () -> assertThat(exist.getCreatedAt().truncatedTo(ChronoUnit.MILLIS))
                                    .isEqualTo(toCreate.getCreatedAt().truncatedTo(ChronoUnit.MILLIS)),
                            () -> assertThat(exist.getUpdatedAt()).isEqualTo(toCreate.getUpdatedAt()).isNull()
                    );
                }
        );
    }

    @Test
    @DisplayName(value = "Успешно обновляем существующий Fragrance")
    public void successfullyUpdateExistFragranceTest() {
        // Arrange
        Fragrance toCreate = FragranceStub.createValidFragrance();
        FragranceID fragranceID = toCreate.getId();
        fragranceRepository.save(toCreate);

        Fragrance toUpdate = Fragrance.builder()
                .id(fragranceID)
                .name(FragranceName.of("Chanel No 5 Eau de Parfum"))
                .resume(FragranceResume.of("Изысканно!"))
                .concentration(FragranceConcentration.EAU_DE_PARFUM)
                .type(Set.of(FragranceType.ALDEHYDE, FragranceType.FLORAL))
                .gender(FragranceGender.FEMALE)
                .season(Set.of(FragranceSeason.DAY, FragranceSeason.NIGHT, FragranceSeason.FALL,
                        FragranceSeason.WINTER))
                .sillage(FragranceSillage.MODERATE)
                .longevity(FragranceLongevity.MIDDLE)
                .availability(FragranceAvailabilityStatus.UNAVAILABLE)
                .rating(FragranceRating.of((byte) 7))
                .createdAt(toCreate.getCreatedAt())
                .updatedAt(Instant.now())
                .build();

        // Act
        fragranceRepository.save(toUpdate);

        // Assert
        assertWith(fragranceRepository.findById(fragranceID), optional -> {
                    Fragrance exist = assertThat(optional).isPresent().get().actual();
                    long existFragranceCount = getCount(exist.getId().getValue().toString());

                    assertAll(
                            () -> assertThat(existFragranceCount).isOne(),
                            () -> assertThat(exist.getId()).isEqualTo(fragranceID),
                            () -> assertThat(exist.getName()).isEqualTo(toUpdate.getName()),
                            () -> assertThat(exist.getResume()).isEqualTo(toUpdate.getResume()),
                            () -> assertThat(exist.getConcentration()).isEqualTo(toUpdate.getConcentration()),
                            () -> assertThat(exist.getType()).containsAll(toUpdate.getType()),
                            () -> assertThat(exist.getGender()).isEqualTo(toUpdate.getGender()),
                            () -> assertThat(exist.getSeason()).containsAll(toUpdate.getSeason()),
                            () -> assertThat(exist.getRating()).isEqualTo(toUpdate.getRating()),
                            () -> assertThat(exist.getAvailability()).isEqualTo(toUpdate.getAvailability()),
                            () -> assertThat(exist.getSillage()).isEqualTo(toUpdate.getSillage()),
                            () -> assertThat(exist.getLongevity()).isEqualTo(toUpdate.getLongevity()),
                            () -> assertThat(exist.getCreatedAt().truncatedTo(ChronoUnit.MILLIS))
                                    .isEqualTo(toUpdate.getCreatedAt().truncatedTo(ChronoUnit.MILLIS)),
                            () -> assertThat(exist.getUpdatedAt().truncatedTo(ChronoUnit.MILLIS))
                                    .isEqualTo(toUpdate.getUpdatedAt().truncatedTo(ChronoUnit.MILLIS))
                    );
                }
        );
    }

    @Test
    @DisplayName(value = "Успешно получаем существующий Fragrance")
    public void successfullyGetExistFragranceTest() {
        // Arrange
        Fragrance toCreate = FragranceStub.createValidFragrance();
        FragranceID fragranceID = toCreate.getId();
        fragranceRepository.save(toCreate);

        // Act
        Optional<Fragrance> fragranceOptional = fragranceRepository.findById(fragranceID);

        // Assert
        Fragrance exist = assertThat(fragranceOptional).isPresent().get().actual();
        assertThat(exist.getId().getValue()).isEqualTo(fragranceID.getValue());
    }

    @Test
    @DisplayName(value = "Не получаем Fragrance, если указан несуществующий ID")
    public void notGetFragranceIfUseNotExistingIdTest() {
        // Arrange
        Fragrance toCreate = FragranceStub.createValidFragrance();
        fragranceRepository.save(toCreate);
        FragranceID notExistFragranceId = FragranceID.of(UUID.randomUUID().toString());

        // Act
        Optional<Fragrance> fragranceOptional = fragranceRepository.findById(notExistFragranceId);

        // Assert
        assertThat(fragranceOptional).isEmpty();
    }

    private long getCount(String fragranceId) {
        return mongoTemplate.count(Query.query(Criteria.where("_id").is(fragranceId)),
                FragranceDBModel.class
        );
    }
}
