package ru.my.scents.boundary.gateway;

import java.util.Optional;
import ru.my.scents.domain.entity.fragrance.Fragrance;
import ru.my.scents.domain.entity.fragrance.FragranceID;

public interface FragranceGateway {

    Optional<Fragrance> findById(FragranceID userID);
}
