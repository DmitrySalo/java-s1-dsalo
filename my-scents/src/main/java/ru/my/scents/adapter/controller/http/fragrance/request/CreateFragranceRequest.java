package ru.my.scents.adapter.controller.http.fragrance.request;

import java.util.Set;
import lombok.Builder;

@Builder
public record CreateFragranceRequest(String name,
                                     Byte rating,
                                     String resume,
                                     String concentration,
                                     Set<String> type,
                                     String gender,
                                     Set<String> season,
                                     String longevity,
                                     String sillage,
                                     String availability) {
}
