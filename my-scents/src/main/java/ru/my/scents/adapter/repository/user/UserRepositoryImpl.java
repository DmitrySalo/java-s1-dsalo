package ru.my.scents.adapter.repository.user;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;
import ru.my.scents.adapter.repository.user.converter.UserConverter;
import ru.my.scents.adapter.repository.user.model.UserDBModel;
import ru.my.scents.boundary.repository.UserRepository;
import ru.my.scents.domain.entity.user.User;
import ru.my.scents.domain.entity.user.UserID;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final MongoTemplate mongoTemplate;

    @Override
    public Optional<User> findById(final UserID userID) {
        if (userID == null) {
            throw new IllegalArgumentException("ID пользователя обязателен!");
        }

        UserDBModel dbModel = mongoTemplate.findById(userID.getValue(), UserDBModel.class);

        return Optional.ofNullable(dbModel).map(UserConverter::toEntity);
    }

    @Override
    public void save(final User user) {
        if (user == null) {
            throw new IllegalArgumentException("Пользователь обязателен!");
        }

        UserDBModel model = UserConverter.toDbModel(user);
        mongoTemplate.save(model);
    }
}
