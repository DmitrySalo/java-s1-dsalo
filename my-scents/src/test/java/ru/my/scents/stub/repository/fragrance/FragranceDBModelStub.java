package ru.my.scents.stub.repository.fragrance;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import lombok.experimental.UtilityClass;
import ru.my.scents.adapter.repository.fragrance.model.FragranceDBModel;
import ru.my.scents.domain.entity.fragrance.FragranceAvailabilityStatus;
import ru.my.scents.domain.entity.fragrance.FragranceConcentration;
import ru.my.scents.domain.entity.fragrance.FragranceGender;
import ru.my.scents.domain.entity.fragrance.FragranceLongevity;
import ru.my.scents.domain.entity.fragrance.FragranceSeason;
import ru.my.scents.domain.entity.fragrance.FragranceSillage;
import ru.my.scents.domain.entity.fragrance.FragranceType;

@UtilityClass
public class FragranceDBModelStub {

    public static FragranceDBModel createFragranceDBModel() {
        return FragranceDBModel.builder()
                .id(UUID.randomUUID().toString())
                .name("Fahrenheit Absolute")
                .resume("Изысканно!")
                .concentration(FragranceConcentration.EAU_DE_TOILETTE.name())
                .type(Set.of(FragranceType.MUSKY.name(), FragranceType.FLORAL.name()))
                .gender(FragranceGender.MALE.name())
                .season(Set.of(FragranceSeason.NIGHT.name(), FragranceSeason.FALL.name(),
                        FragranceSeason.WINTER.name()))
                .sillage(FragranceSillage.STRONG.name())
                .longevity(FragranceLongevity.STRONG.name())
                .availability(FragranceAvailabilityStatus.AVAILABLE.name())
                .rating((byte) 8)
                .createdAt(Instant.parse("2026-01-22T11:00:00Z"))
                .updatedAt(Instant.parse("2026-01-22T12:00:00Z"))
                .build();
    }

    public static FragranceDBModel createFragranceDBModelWithId(String id) {
        FragranceDBModel model = createFragranceDBModel();
        model.setId(id);
        return model;
    }
}