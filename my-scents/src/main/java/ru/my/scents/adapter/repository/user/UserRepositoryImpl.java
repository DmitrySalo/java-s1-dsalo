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
        UserDBModel dbModel = mongoTemplate.findById(userID.getValue(), UserDBModel.class);
        return Optional.ofNullable(dbModel).map(UserConverter::toEntity);
    }

    @Override
    public void save(final User user) {
        UserDBModel model = UserConverter.toDbModel(user);
        mongoTemplate.save(model);
    }

    @Override
    public void delete(final UserID userID) {
        UserDBModel dbModel = mongoTemplate.findById(userID.getValue(), UserDBModel.class);
        Optional.ofNullable(dbModel)
                .map(mongoTemplate::remove)
                .orElseThrow(() -> new IllegalStateException("Пользователь c ID {%s} не существует!"
                        .formatted(userID.getValue())));
    }
}
