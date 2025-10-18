package ru.my.scents.domain.usecase.user;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;
import ru.my.scents.boundary.model.CreateUserParam;
import ru.my.scents.boundary.model.User;
import ru.my.scents.boundary.usecase.CreateUserUseCase;

@Service
public class UserUseCaseImpl implements CreateUserUseCase {

    private final Map<String, User> bookings = new HashMap<>();

    @Override
    public User execute(CreateUserParam params) {

        User result = User.builder()
                .id(UUID.randomUUID().toString())
                .firstName(params.getFirstName())
                .lastName(params.getLastName())
                .email(params.getEmail())
                .phoneNumber(params.getPhoneNumber())
                .createdAt(LocalDateTime.now())
                .build();
        bookings.put(result.getId(), result);
        return result;
    }
}
