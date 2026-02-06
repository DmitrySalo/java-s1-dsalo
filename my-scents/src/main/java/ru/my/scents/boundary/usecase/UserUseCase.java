package ru.my.scents.boundary.usecase;

import ru.my.scents.boundary.model.user.CreateUserParam;
import ru.my.scents.boundary.model.user.UpdateUserParam;
import ru.my.scents.domain.entity.user.User;

public interface UserUseCase {

    User create(CreateUserParam params);

    User update(UpdateUserParam params);

    User get(String userId);
}
