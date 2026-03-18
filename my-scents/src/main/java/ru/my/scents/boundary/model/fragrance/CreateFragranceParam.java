package ru.my.scents.boundary.model.fragrance;

import java.time.Instant;
import java.util.Set;
import lombok.Builder;

@Builder
public record CreateFragranceParam(String id,
                                   String name,
                                   Byte rating,
                                   String concentration,
                                   String resume,
                                   Set<String> type,
                                   String gender,
                                   Set<String> season,
                                   String longevity,
                                   String sillage,
                                   String availability,
                                   Instant createdAt) {
}
