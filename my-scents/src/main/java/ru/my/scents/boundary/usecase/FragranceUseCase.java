package ru.my.scents.boundary.usecase;

import ru.my.scents.boundary.model.fragrance.CreateFragranceParam;
import ru.my.scents.boundary.model.fragrance.UpdateFragranceParam;
import ru.my.scents.domain.entity.fragrance.Fragrance;

public interface FragranceUseCase {

    Fragrance create(CreateFragranceParam params);

    Fragrance update(UpdateFragranceParam params);

    Fragrance get(String fragranceId);

    void delete(String fragranceId);
}
