package ru.my.scents.boundary.usecase;

import ru.my.scents.boundary.model.CreateUserParam;
import ru.my.scents.boundary.model.User;

public interface CreateUserUseCase {

    User execute(CreateUserParam params);
}
