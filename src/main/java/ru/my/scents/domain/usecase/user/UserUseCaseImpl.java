package ru.my.scents.domain.usecase.user;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;
import ru.my.scents.boundary.model.CreateUserParam;
import ru.my.scents.boundary.model.UpdateUserParam;
import ru.my.scents.domain.entity.User;
import ru.my.scents.boundary.usecase.UserUseCase;
import ru.my.scents.domain.entity.UserEmail;
import ru.my.scents.domain.entity.UserID;
import ru.my.scents.domain.entity.UserName;
import ru.my.scents.domain.entity.UserPhoneNumber;

@Service
public class UserUseCaseImpl implements UserUseCase {

    private final Map<UserID, User> bookings = new HashMap<>();

    @Override
    public User create(CreateUserParam params) {

        User result = User.builder()
                .id(UserID.of(UUID.randomUUID().toString()))
                .firstName(UserName.of(params.getFirstName()))
                .lastName(UserName.of(params.getLastName()))
                .email(UserEmail.of(params.getEmail()))
                .phoneNumber(UserPhoneNumber.of(params.getPhoneNumber()))
                .createdAt(LocalDateTime.now())
                .build();
        bookings.put(result.getId(), result);
        return result;
    }

    @Override
    public User update(UpdateUserParam params) {
        User user = this.get(params.getId());

        User result = User.builder()
                .id(user.getId())
                .firstName(UserName.of(params.getFirstName()))
                .lastName(UserName.of(params.getLastName()))
                .email(UserEmail.of(params.getEmail()))
                .phoneNumber(UserPhoneNumber.of(params.getPhoneNumber()))
                .createdAt(user.getCreatedAt())
                .updatedAt(params.getUpdatedAt())
                .build();

        bookings.put(result.getId(), result);

        return result;
    }

    @Override
    public User get(String userId) {
        UserID id = UserID.of(userId);
        User user = bookings.get(id);
        if (user == null) {
            throw new EntityNotFoundException(userId);
        }
        return user;
    }
}
