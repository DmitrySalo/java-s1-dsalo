package ru.my.scents.boundary.usecase;

import ru.my.scents.boundary.model.CreateUserParam;
import ru.my.scents.boundary.model.UpdateUserParam;
import ru.my.scents.domain.entity.User;

public interface UserUseCase {

    User create(CreateUserParam params);

    User update(UpdateUserParam params);

    User get(String userId);
}
