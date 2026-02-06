package ru.my.scents.domain.entity.fragrance;

import java.time.Instant;
import java.util.Objects;
import java.util.Set;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Fragrance {

    private final FragranceID id;
    private final FragranceName name;
    private final FragranceRating rating;
    private final FragranceResume resume;
    private final Set<FragranceType> type;
    private final FragranceGender gender;
    private final Set<FragranceSeason> season;
    private final FragranceLongevity longevity;
    private final FragranceSillage sillage;
    private final FragranceAvailabilityStatus availability;
    private final Instant createdAt;
    private final Instant updatedAt;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Fragrance fragrance = (Fragrance) o;
        return Objects.equals(id, fragrance.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
