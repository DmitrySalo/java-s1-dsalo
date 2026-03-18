package ru.my.scents.boundary.repository;

import java.util.Optional;
import ru.my.scents.domain.entity.fragrance.Fragrance;
import ru.my.scents.domain.entity.fragrance.FragranceID;

public interface FragranceRepository {

    Optional<Fragrance> findById(FragranceID userID);

    void save(Fragrance user);

    void delete(FragranceID userID);
}
