package ru.my.scents.boundary.model;

import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class CreateUserParam {

    private final String id;
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String phoneNumber;
    private final LocalDateTime createdAt;

    public CreateUserParam(String id, String firstName, String lastName, String email, String phoneNumber,
                           LocalDateTime createdAt) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.createdAt = createdAt;
    }
}
