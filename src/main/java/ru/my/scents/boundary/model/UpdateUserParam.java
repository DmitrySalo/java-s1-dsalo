package ru.my.scents.boundary.model;

import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class UpdateUserParam {

    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private LocalDateTime updatedAt;

    public UpdateUserParam(String id, String firstName, String lastName, String email, String phoneNumber,
                           LocalDateTime updatedAt) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.updatedAt = updatedAt;
    }
}
