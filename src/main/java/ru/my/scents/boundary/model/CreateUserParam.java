package ru.my.scents.boundary.model;

import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class CreateUserParam {

    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private LocalDateTime createdAt;

    public CreateUserParam(String id, String firstName, String lastName, String email, String phoneNumber, LocalDateTime createdAt) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.createdAt = createdAt;
    }
}
