package ru.my.scents.adapter.repository.user.model;


import java.time.Instant;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Builder
@Getter
@Setter
@Document(collection = "user")
@CompoundIndex(
        name = "name_email_phone_number_idx",
        def = "{'firstName': 1, 'lastName': 1, 'email': 1, 'phoneNumber': 1}"
)
public class UserDBModel {

    @Id
    @Field(name = "id")
    private String id;

    @Field(name = "firstName")
    private String firstName;

    @Field(name = "lastName")
    private String lastName;

    @Field(name = "email")
    private String email;

    @Field(name = "phoneNumber")
    private String phoneNumber;

    @Field(name = "createdAt")
    private Instant createdAt;

    @Field(name = "updatedAt")
    private Instant updatedAt;
}
