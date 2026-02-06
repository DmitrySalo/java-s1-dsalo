package ru.my.scents.boundary.model.user;

import java.time.Instant;
import lombok.Builder;

@Builder
public record UpdateUserParam(String id, String firstName, String lastName, String email, String phoneNumber,
                              Instant updatedAt) {

}
