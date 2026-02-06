package ru.my.scents.domain.usecase.user;

import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.my.scents.boundary.gateway.FragranceGateway;
import ru.my.scents.boundary.model.user.CreateUserParam;
import ru.my.scents.boundary.model.user.UpdateUserParam;
import ru.my.scents.boundary.repository.UserRepository;
import ru.my.scents.boundary.usecase.UserUseCase;
import ru.my.scents.domain.entity.user.User;
import ru.my.scents.domain.entity.user.UserEmail;
import ru.my.scents.domain.entity.user.UserID;
import ru.my.scents.domain.entity.user.UserName;
import ru.my.scents.domain.entity.user.UserPhoneNumber;
import ru.my.scents.infra.logger.Logger;

@Service
@RequiredArgsConstructor
public class UserUseCaseImpl implements UserUseCase {

    private final Logger logger;
    private final FragranceGateway fragranceGateway;
    private final UserRepository userRepository;

    @Override
    public User create(CreateUserParam params) {

        User user = User.builder()
                .id(UserID.of(params.id()))
                .firstName(UserName.of(params.firstName()))
                .lastName(UserName.of(params.lastName()))
                .email(UserEmail.of(params.email()))
                .phoneNumber(UserPhoneNumber.of(params.phoneNumber()))
                .createdAt(Instant.now())
                .build();

        userRepository.save(user);
        logger.info("Пользователь создан с ID {}", user.getId());

        return user;
    }

    @Override
    public User update(UpdateUserParam params) {
        User user = this.get(params.id());

        user = User.builder()
                .id(user.getId())
                .firstName(UserName.of(params.firstName()))
                .lastName(UserName.of(params.lastName()))
                .email(UserEmail.of(params.email()))
                .phoneNumber(UserPhoneNumber.of(params.phoneNumber()))
                .createdAt(user.getCreatedAt())
                .updatedAt(params.updatedAt())
                .build();

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
}
