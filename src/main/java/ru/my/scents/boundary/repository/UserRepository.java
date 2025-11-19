package ru.my.scents.boundary.repository;

import java.util.Optional;
import ru.my.scents.domain.entity.User;
import ru.my.scents.domain.entity.UserID;

public interface UserRepository {

    Optional<User> findById(UserID userID);

    void save(User user);
}
