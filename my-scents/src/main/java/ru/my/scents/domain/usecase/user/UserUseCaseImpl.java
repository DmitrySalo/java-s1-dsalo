package ru.my.scents.domain.usecase.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.my.scents.boundary.model.user.CreateUserParam;
import ru.my.scents.boundary.model.user.UpdateUserParam;
import ru.my.scents.boundary.repository.UserRepository;
import ru.my.scents.boundary.usecase.UserUseCase;
import ru.my.scents.domain.entity.user.User;
import ru.my.scents.domain.entity.user.UserID;
import ru.my.scents.domain.usecase.user.converter.UserConverter;
import ru.my.scents.infra.logger.Logger;

@Service
@RequiredArgsConstructor
public class UserUseCaseImpl implements UserUseCase {

    private final Logger logger;
    private final UserRepository userRepository;

    @Override
    public User create(CreateUserParam params) {
        User user = UserConverter.toEntity(params);
        userRepository.save(user);
        logger.info("Пользователь создан с ID {}", user.getId());
        return user;
    }

    @Override
    public User update(UpdateUserParam params) {
        User user = UserConverter.toEntity(params);
        userRepository.save(user);
        logger.info("Пользователь обновлён с ID {}", user.getId());
        return user;
    }

    @Override
    public User get(String userId) {
        UserID id = UserID.of(userId);
        return userRepository.findById(id)
                .orElseThrow(() -> new NullPointerException(userId));
    }

    @Override
    public void delete(String userId) {
        UserID id = UserID.of(userId);
        userRepository.delete(id);
        logger.info("Пользователь с ID {} удалён", id.getValue());
    }
}
