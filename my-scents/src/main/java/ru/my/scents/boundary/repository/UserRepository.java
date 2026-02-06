package ru.my.scents.boundary.repository;

import java.util.Optional;
import ru.my.scents.domain.entity.user.User;
import ru.my.scents.domain.entity.user.UserID;

public interface UserRepository {

    Optional<User> findById(UserID userID);

    void save(User user);
}
