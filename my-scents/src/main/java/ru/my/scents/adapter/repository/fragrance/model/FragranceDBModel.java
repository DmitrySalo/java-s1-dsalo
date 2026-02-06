package ru.my.scents.adapter.repository.fragrance.model;


import java.time.Instant;
import java.util.Set;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Builder
@Getter
@Setter
@Document(collection = "fragrance")
public class FragranceDBModel {

    @Id
    @Field(name = "id")
    private String id;

    @Indexed(name = "name_idx")
    @Field(name = "name")
    private String name;

    @Field(name = "rating")
    private Byte rating;

    @Field(name = "resume")
    private String resume;

    @Indexed(name = "type_idx")
    @Field(name = "type")
    private Set<String> type;

    @Indexed(name = "gender_idx")
    @Field(name = "gender")
    private String gender;

    @Indexed(name = "season_idx")
    @Field(name = "season")
    private Set<String> season;

    @Field(name = "longevity")
    private String longevity;

    @Field(name = "sillage")
    private String sillage;

    @Indexed(name = "availability_idx")
    @Field(name = "availability")
    private String availability;

    @Field(name = "createdAt")
    private Instant createdAt;

    @Field(name = "updatedAt")
    private Instant updatedAt;
}
