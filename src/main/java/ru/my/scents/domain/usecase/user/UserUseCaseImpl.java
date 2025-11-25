package ru.my.scents.domain.usecase.user;

import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.my.scents.boundary.model.CreateUserParam;
import ru.my.scents.boundary.model.UpdateUserParam;
import ru.my.scents.boundary.repository.UserRepository;
import ru.my.scents.boundary.usecase.UserUseCase;
import ru.my.scents.domain.entity.User;
import ru.my.scents.domain.entity.UserEmail;
import ru.my.scents.domain.entity.UserID;
import ru.my.scents.domain.entity.UserName;
import ru.my.scents.domain.entity.UserPhoneNumber;
import ru.my.scents.infra.logger.Logger;

@Service
@RequiredArgsConstructor
public class UserUseCaseImpl implements UserUseCase {

    private final Logger logger;
    private final UserRepository userRepository;

    @Override
    public User create(CreateUserParam params) {

        User user = User.builder()
                .id(UserID.of(params.getId()))
                .firstName(UserName.of(params.getFirstName()))
                .lastName(UserName.of(params.getLastName()))
                .email(UserEmail.of(params.getEmail()))
                .phoneNumber(UserPhoneNumber.of(params.getPhoneNumber()))
                .createdAt(Instant.now())
                .build();

        userRepository.save(user);
        logger.info(String.format("User created with id %s", user.getId()));

        return user;
    }

    @Override
    public User update(UpdateUserParam params) {
        User user = this.get(params.getId());

        user = User.builder()
                .id(user.getId())
                .firstName(UserName.of(params.getFirstName()))
                .lastName(UserName.of(params.getLastName()))
                .email(UserEmail.of(params.getEmail()))
                .phoneNumber(UserPhoneNumber.of(params.getPhoneNumber()))
                .createdAt(user.getCreatedAt())
                .updatedAt(params.getUpdatedAt())
                .build();

        userRepository.save(user);
        logger.info(String.format("User updated with id %s", user.getId()));

        return user;
    }

    @Override
    public User get(String userId) {
        UserID id = UserID.of(userId);
        return userRepository.findById(id)
                .orElseThrow(() -> new NullPointerException(userId));
    }
}
