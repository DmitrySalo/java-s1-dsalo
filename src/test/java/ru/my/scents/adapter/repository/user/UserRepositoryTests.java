package ru.my.scents.adapter.repository.user;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertWith;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import ru.my.scents.adapter.repository.user.model.UserDBModel;
import ru.my.scents.boundary.repository.UserRepository;
import ru.my.scents.configuration.BaseIntegrationTest;
import ru.my.scents.domain.entity.User;
import ru.my.scents.domain.entity.UserEmail;
import ru.my.scents.domain.entity.UserID;
import ru.my.scents.domain.entity.UserName;
import ru.my.scents.domain.entity.UserPhoneNumber;
import ru.my.scents.stub.entity.UserStub;

@DisplayName(value = "Интеграционные тесты UserRepository")
public class UserRepositoryTests extends BaseIntegrationTest {

    private final MongoTemplate mongoTemplate;
    private final UserRepository userRepository;

    @Autowired
    UserRepositoryTests(MongoTemplate mongoTemplate, UserRepository userRepository) {
        this.mongoTemplate = mongoTemplate;
        this.userRepository = userRepository;
    }

    @AfterEach
    void cleanUp() {
        mongoTemplate.getCollectionNames().forEach(mongoTemplate::dropCollection);
    }

    @Test
    @DisplayName(value = "Успешно сохраняем нового User")
    public void successfullySaveNewUserTest() {
        // Arrange
        User toCreate = UserStub.createValidUser();
        UserID userID = toCreate.getId();

        // Act
        userRepository.save(toCreate);

        // Assert
        assertWith(userRepository.findById(userID), optional -> {
                    User exist = assertThat(optional).isPresent().get().actual();
                    long existUserCount = getCount(exist.getId().getValue().toString());

                    assertAll(
                            () -> assertThat(existUserCount).isOne(),
                            () -> assertThat(exist.getId()).isEqualTo(userID),
                            () -> assertThat(exist.getFirstName()).isEqualTo(toCreate.getFirstName()),
                            () -> assertThat(exist.getLastName()).isEqualTo(toCreate.getLastName()),
                            () -> assertThat(exist.getEmail()).isEqualTo(toCreate.getEmail()),
                            () -> assertThat(exist.getPhoneNumber()).isEqualTo(toCreate.getPhoneNumber()),
                            () -> assertThat(exist.getCreatedAt().truncatedTo(ChronoUnit.MILLIS))
                                    .isEqualTo(toCreate.getCreatedAt().truncatedTo(ChronoUnit.MILLIS)),
                            () -> assertThat(exist.getUpdatedAt()).isEqualTo(toCreate.getUpdatedAt()).isNull()
                    );
                }
        );
    }

    @Test
    @DisplayName(value = "Успешно обновляем существующего User")
    public void successfullyUpdateExistUserTest() {
        // Arrange
        User toCreate = UserStub.createValidUser();
        UserID userID = toCreate.getId();
        userRepository.save(toCreate);

        User toUpdate = User.builder()
                .id(userID)
                .firstName(UserName.of("Foma"))
                .lastName(UserName.of("Kinaev"))
                .email(UserEmail.of("mrborn@mail.ru"))
                .phoneNumber(UserPhoneNumber.of("89275550101"))
                .createdAt(toCreate.getCreatedAt())
                .updatedAt(Instant.now())
                .build();

        // Act
        userRepository.save(toUpdate);

        // Assert
        assertWith(userRepository.findById(userID), optional -> {
                    User exist = assertThat(optional).isPresent().get().actual();
                    long existUserCount = getCount(exist.getId().getValue().toString());

                    assertAll(
                            () -> assertThat(existUserCount).isOne(),
                            () -> assertThat(exist.getId()).isEqualTo(userID),
                            () -> assertThat(exist.getFirstName()).isEqualTo(toUpdate.getFirstName()),
                            () -> assertThat(exist.getLastName()).isEqualTo(toUpdate.getLastName()),
                            () -> assertThat(exist.getEmail()).isEqualTo(toUpdate.getEmail()),
                            () -> assertThat(exist.getPhoneNumber()).isEqualTo(toUpdate.getPhoneNumber()),
                            () -> assertThat(exist.getCreatedAt().truncatedTo(ChronoUnit.MILLIS))
                                    .isEqualTo(toUpdate.getCreatedAt().truncatedTo(ChronoUnit.MILLIS)),
                            () -> assertThat(exist.getUpdatedAt().truncatedTo(ChronoUnit.MILLIS))
                                    .isEqualTo(toUpdate.getUpdatedAt().truncatedTo(ChronoUnit.MILLIS))
                    );
                }
        );
    }

    @Test
    @DisplayName(value = "Успешно получаем существующего User")
    public void successfullyGetExistUserTest() {
        // Arrange
        User toCreate = UserStub.createValidUser();
        UserID userID = toCreate.getId();
        userRepository.save(toCreate);

        // Act
        Optional<User> userOptional = userRepository.findById(userID);

        // Assert
        User exist = assertThat(userOptional).isPresent().get().actual();
        assertThat(exist.getId().getValue()).isEqualTo(userID.getValue());
    }

    @Test
    @DisplayName(value = "Не получаем User, если указан несуществующий ID")
    public void notGetUserIfUseNotExistingIdTest() {
        // Arrange
        User toCreate = UserStub.createValidUser();
        userRepository.save(toCreate);
        UserID notExistUserId = UserID.of(UUID.randomUUID().toString());

        // Act
        Optional<User> userOptional = userRepository.findById(notExistUserId);

        // Assert
        assertThat(userOptional).isEmpty();
    }

    private long getCount(String userId) {
        return mongoTemplate.count(Query.query(Criteria.where("_id").is(userId)),
                UserDBModel.class
        );
    }
}
