package ru.my.scents.adapter.gateway.grpc.fragrance.converter;

import com.google.protobuf.Timestamp;
import java.util.Set;
import java.util.stream.Collectors;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.assertj.core.api.ThrowableAssert;
import static org.junit.jupiter.api.Assertions.assertAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import ru.my.scents.configuration.BaseTest;
import ru.my.scents.domain.entity.fragrance.Fragrance;
import ru.my.scents.domain.entity.fragrance.FragranceAvailabilityStatus;
import ru.my.scents.domain.entity.fragrance.FragranceConcentration;
import ru.my.scents.domain.entity.fragrance.FragranceGender;
import ru.my.scents.domain.entity.fragrance.FragranceLongevity;
import ru.my.scents.domain.entity.fragrance.FragranceSillage;
import ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceData;
import ru.my.scents.stub.entity.FragranceStub;
import ru.my.scents.stub.proto.FragranceDataStub;

@DisplayName(value = "Интеграционные тесты FragranceProtoConverter")
public class FragranceProtoConverterTests extends BaseTest {

    @DisplayName(value = "Успешно конвертируем entity в proto, если entity валиден")
    @Test
    public void successfullyConvertedToProto() {

        Fragrance fragrance = FragranceStub.createValidFragrance();

        FragranceData fragranceData = FragranceProtoConverter.toProto(fragrance);

        assertThat(fragranceData).isNotNull();
        assertAll(
                () -> assertThat(fragranceData.getId()).isEqualTo(fragrance.getId().getValue().toString()),
                () -> assertThat(fragranceData.getName()).isEqualTo(fragrance.getName().getValue()),
                () -> assertThat(fragranceData.getResume()).isEqualTo(fragrance.getResume().getValue()),
                () -> assertThat(fragranceData.getConcentration().name()).contains(fragrance.getConcentration().name()),
                () -> assertThat(fragranceData.getRating()).isEqualTo(fragrance.getRating().getValue()),
                () -> assertThat(fragranceData.getGender().name()).contains(fragrance.getGender().name()),
                () -> assertThat(fragranceData.getAvailability().name()).contains(fragrance.getAvailability().name()),
                () -> assertThat(fragranceData.getLongevity().name()).contains(fragrance.getLongevity().name()),
                () -> assertThat(fragranceData.getSillage().name()).contains(fragrance.getSillage().name()),
                () -> assertThat(isSameSeasons(fragranceData, fragrance)).isTrue(),
                () -> assertThat(isSameTypes(fragranceData, fragrance)).isTrue(),
                () -> assertThat(fragranceData.getCreatedAt().getNanos()).isEqualTo(fragrance.getCreatedAt().getNano()),
                () -> assertThat(fragrance.getUpdatedAt()).isNull(),
                () -> assertThat(fragranceData.getUpdatedAt().getNanos()).isZero()
        );
    }

    @DisplayName(value = "Успешно конвертируем proto в entity, если proto валиден")
    @Test
    public void successfullyConvertedToEntity() {

        FragranceData fragranceData = FragranceDataStub.createValidFragrance();

        Fragrance fragrance = FragranceProtoConverter.toDomain(fragranceData);

        assertThat(fragrance).isNotNull();
        assertAll(
                () -> assertThat(fragranceData.getId()).isEqualTo(fragrance.getId().getValue().toString()),
                () -> assertThat(fragranceData.getName()).isEqualTo(fragrance.getName().getValue()),
                () -> assertThat(fragranceData.getResume()).isEqualTo(fragrance.getResume().getValue()),
                () -> assertThat(fragranceData.getConcentration().name()).contains(fragrance.getConcentration().name()),
                () -> assertThat(fragranceData.getRating()).isEqualTo(fragrance.getRating().getValue()),
                () -> assertThat(fragranceData.getGender().name()).contains(fragrance.getGender().name()),
                () -> assertThat(fragranceData.getAvailability().name()).contains(fragrance.getAvailability().name()),
                () -> assertThat(fragranceData.getLongevity().name()).contains(fragrance.getLongevity().name()),
                () -> assertThat(fragranceData.getSillage().name()).contains(fragrance.getSillage().name()),
                () -> assertThat(isSameSeasons(fragranceData, fragrance)).isTrue(),
                () -> assertThat(isSameTypes(fragranceData, fragrance)).isTrue(),
                () -> assertThat(fragranceData.getCreatedAt().getNanos()).isEqualTo(fragrance.getCreatedAt().getNano()),
                () -> assertThat(fragranceData.getUpdatedAt().getNanos()).isEqualTo(fragrance.getUpdatedAt().getNano())
        );
    }

    @DisplayName(value = "Выбрасываем IllegalArgumentException при конвертации null entity в proto")
    @Test
    public void throwsExceptionWhenEntityIsNullTest() {

        // Act
        ThrowableAssert.ThrowingCallable act = () -> FragranceProtoConverter.toProto(null);

        // Assert
        assertThatThrownBy(act)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Парфюм обязателен!");
    }

    @DisplayName(value = "Выбрасываем IllegalArgumentException при конвертации null proto в entity")
    @Test
    public void throwsExceptionWhenProtoIsNullTest() {

        // Act
        ThrowableAssert.ThrowingCallable act = () -> FragranceProtoConverter.toDomain(null);

        // Assert
        assertThatThrownBy(act)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Парфюм обязателен!");
    }

    @DisplayName(value = "FragranceType конвертируется в null, если proto имеет значение FRAGRANCE_TYPE_UNSPECIFIED")
    @Test
    public void typeConvertedToNullWhenUnspecifiedTest() {
        FragranceData fragranceData = FragranceDataStub.createFragranceWithType(
                ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceType.FRAGRANCE_TYPE_UNSPECIFIED);

        Fragrance fragrance = FragranceProtoConverter.toDomain(fragranceData);

        assertThat(fragrance).isNotNull();
        assertThat(fragrance.getType()).isEmpty();
    }

    @DisplayName(value = "FragranceConcentration конвертируется в null, если proto имеет значение FRAGRANCE_CONCENTRATION_UNSPECIFIED")
    @Test
    public void typeConvertedToNullWhenFragranceConcentrationUnspecifiedTest() {
        FragranceData fragranceData = FragranceDataStub.createFragranceWithConcentration(
                ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceConcentration.FRAGRANCE_CONCENTRATION_UNSPECIFIED);

        Fragrance fragrance = FragranceProtoConverter.toDomain(fragranceData);

        assertThat(fragrance).isNotNull();
        assertThat(fragrance.getConcentration()).isNull();
    }

    @DisplayName(value = "FragranceGender конвертируется в null, если proto имеет значение FRAGRANCE_GENDER_UNSPECIFIED")
    @Test
    public void genderConvertedToNullWhenUnspecifiedTest() {
        FragranceData fragranceData = FragranceDataStub.createFragranceWithGender(
                ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceGender.FRAGRANCE_GENDER_UNSPECIFIED);

        Fragrance fragrance = FragranceProtoConverter.toDomain(fragranceData);

        assertThat(fragrance).isNotNull();
        assertThat(fragrance.getGender()).isNull();
    }

    @DisplayName(value = "FragranceSeason конвертируется в пустой Set, если proto содержит только FRAGRANCE_SEASON_UNSPECIFIED")
    @Test
    public void seasonConvertedToEmptySetWhenUnspecifiedTest() {
        FragranceData fragranceData = FragranceDataStub.createFragranceWithSeasons(
                Set.of(ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceSeason.FRAGRANCE_SEASON_UNSPECIFIED));

        Fragrance fragrance = FragranceProtoConverter.toDomain(fragranceData);

        assertThat(fragrance).isNotNull();
        assertThat(fragrance.getSeason()).isEmpty();
    }

    @DisplayName(value = "FragranceLongevity конвертируется в null, если proto имеет значение FRAGRANCE_LONGEVITY_UNSPECIFIED")
    @Test
    public void longevityConvertedToNullWhenUnspecifiedTest() {
        FragranceData fragranceData = FragranceDataStub.createFragranceWithLongevity(
                ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceLongevity.FRAGRANCE_LONGEVITY_UNSPECIFIED);

        Fragrance fragrance = FragranceProtoConverter.toDomain(fragranceData);

        assertThat(fragrance).isNotNull();
        assertThat(fragrance.getLongevity()).isNull();
    }

    @DisplayName(value = "FragranceSillage конвертируется в null, если proto имеет значение FRAGRANCE_SILLAGE_UNSPECIFIED")
    @Test
    public void sillageConvertedToNullWhenUnspecifiedTest() {
        FragranceData fragranceData = FragranceDataStub.createFragranceWithSillage(
                ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceSillage.FRAGRANCE_SILLAGE_UNSPECIFIED);

        Fragrance fragrance = FragranceProtoConverter.toDomain(fragranceData);

        assertThat(fragrance).isNotNull();
        assertThat(fragrance.getSillage()).isNull();
    }

    @DisplayName(value = "FragranceAvailabilityStatus конвертируется в null, если proto имеет значение FRAGRANCE_AVAILABILITY_STATUS_UNSPECIFIED")
    @Test
    public void availabilityConvertedToNullWhenUnspecifiedTest() {
        FragranceData fragranceData = FragranceDataStub.createFragranceWithAvailability(
                ru.my.scents.fragrance.adapter.controller.grpc.fragrance.proto.FragranceAvailabilityStatus.FRAGRANCE_AVAILABILITY_STATUS_UNSPECIFIED);

        Fragrance fragrance = FragranceProtoConverter.toDomain(fragranceData);

        assertThat(fragrance).isNotNull();
        assertThat(fragrance.getAvailability()).isNull();
    }

    @DisplayName(value = "Timestamp конвертируется в null, если proto содержит default instance")
    @Test
    public void timestampConvertedToNullWhenDefaultTest() {
        FragranceData fragranceData = FragranceDataStub.createFragranceWithDefaultTimestamps();

        Fragrance fragrance = FragranceProtoConverter.toDomain(fragranceData);

        assertThat(fragrance).isNotNull();
        assertAll(
                () -> assertThat(fragrance.getCreatedAt()).isNull(),
                () -> assertThat(fragrance.getUpdatedAt()).isNull()
        );
    }

    @DisplayName(value = "Успешно конвертируем entity с updatedAt в proto")
    @Test
    public void successfullyConvertedToProtoWithUpdatedAtTest() {
        Fragrance fragrance = FragranceStub.createFragranceWithUpdatedAt();

        FragranceData fragranceData = FragranceProtoConverter.toProto(fragrance);

        assertAll(
                () -> assertThat(fragranceData.getId()).isEqualTo(fragrance.getId().getValue().toString()),
                () -> assertThat(fragranceData.getCreatedAt()).isNotEqualTo(Timestamp.getDefaultInstance()),
                () -> assertThat(fragranceData.getUpdatedAt()).isNotEqualTo(Timestamp.getDefaultInstance()),
                () -> assertThat(fragranceData.getCreatedAt().getSeconds()).isEqualTo(fragrance.getCreatedAt().getEpochSecond()),
                () -> assertThat(fragranceData.getUpdatedAt().getSeconds()).isEqualTo(fragrance.getUpdatedAt().getEpochSecond())
        );
    }

    @DisplayName(value = "Успешно конвертируем все значения FragranceType из entity в proto")
    @Test
    public void successfullyConvertedAllTypesToProtoTest() {
        Fragrance fragrance = FragranceStub.createFragranceWithAllTypes();

        FragranceData fragranceData = FragranceProtoConverter.toProto(fragrance);

        assertThat(isSameTypes(fragranceData, fragrance)).isTrue();
    }

    @DisplayName(value = "Успешно конвертируем все значения FragranceSeason из entity в proto")
    @Test
    public void successfullyConvertedAllSeasonsToProtoTest() {
        Fragrance fragrance = FragranceStub.createFragranceWithAllSeasons();

        FragranceData fragranceData = FragranceProtoConverter.toProto(fragrance);

        assertThat(isSameSeasons(fragranceData, fragrance)).isTrue();
    }

    @DisplayName(value = "Успешно конвертируем все значения FragranceGender из entity в proto")
    @ParameterizedTest
    @EnumSource(FragranceGender.class)
    void successfullyConvertedAllGendersToProtoTest(FragranceGender gender) {
        Fragrance fragrance = FragranceStub.createFragranceWithGender(gender);

        FragranceData fragranceData = FragranceProtoConverter.toProto(fragrance);

        assertThat(fragranceData.getGender().name()).contains(gender.name());
    }

    @DisplayName(value = "Успешно конвертируем все значения FragranceConcentration из entity в proto")
    @ParameterizedTest
    @EnumSource(FragranceConcentration.class)
    void successfullyConvertedAllConcentrationsToProtoTest(FragranceConcentration concentration) {
        Fragrance fragrance = FragranceStub.createFragranceWithConcentration(concentration);

        FragranceData fragranceData = FragranceProtoConverter.toProto(fragrance);

        assertThat(fragranceData.getConcentration().name()).contains(concentration.name());
    }

    @DisplayName(value = "Успешно конвертируем все значения FragranceLongevity из entity в proto")
    @ParameterizedTest
    @EnumSource(FragranceLongevity.class)
    void successfullyConvertedAllLongevitiesToProtoTest(FragranceLongevity longevity) {
        Fragrance fragrance = FragranceStub.createFragranceWithLongevity(longevity);

        FragranceData fragranceData = FragranceProtoConverter.toProto(fragrance);

        assertThat(fragranceData.getLongevity().name()).contains(longevity.name());
    }

    @DisplayName(value = "Успешно конвертируем все значения FragranceSillage из entity в proto")
    @ParameterizedTest
    @EnumSource(FragranceSillage.class)
    void successfullyConvertedAllSillagesToProtoTest(FragranceSillage sillage) {
        Fragrance fragrance = FragranceStub.createFragranceWithSillage(sillage);

        FragranceData fragranceData = FragranceProtoConverter.toProto(fragrance);

        assertThat(fragranceData.getSillage().name()).contains(sillage.name());
    }

    @DisplayName(value = "Успешно конвертируем все значения FragranceAvailabilityStatus из entity в proto")
    @ParameterizedTest
    @EnumSource(FragranceAvailabilityStatus.class)
    void successfullyConvertedAllAvailabilitiesToProtoTest(FragranceAvailabilityStatus availability) {
        Fragrance fragrance = FragranceStub.createFragranceWithAvailability(availability);

        FragranceData fragranceData = FragranceProtoConverter.toProto(fragrance);

        assertThat(fragranceData.getAvailability().name()).contains(availability.name());
    }

    @DisplayName(value = "Пустой Set типов конвертируется в пустой список в proto")
    @Test
    public void emptyTypesConvertedToEmptyListInProtoTest() {
        Fragrance fragrance = FragranceStub.createFragranceWithEmptyTypes();

        FragranceData fragranceData = FragranceProtoConverter.toProto(fragrance);

        assertThat(fragranceData.getTypeList()).isEmpty();
    }

    @DisplayName(value = "Пустой Set сезонов конвертируется в пустой список в proto")
    @Test
    public void emptySeasonsConvertedToEmptyListInProtoTest() {
        Fragrance fragrance = FragranceStub.createFragranceWithEmptySeasons();

        FragranceData fragranceData = FragranceProtoConverter.toProto(fragrance);

        assertThat(fragranceData.getSeasonList()).isEmpty();
    }

    private boolean isSameSeasons(FragranceData fragranceData, Fragrance fragrance) {

        Set<String> protoSeasons = fragranceData.getSeasonList().stream()
                .map(Enum::name)
                .collect(Collectors.toSet());

        Set<String> entitySeasons = fragrance.getSeason().stream()
                .map(Enum::name)
                .collect(Collectors.toSet());

        return isSameParameters(protoSeasons, entitySeasons);
    }

    private boolean isSameTypes(FragranceData fragranceData, Fragrance fragrance) {

        Set<String> protoTypes = fragranceData.getTypeList().stream()
                .map(Enum::name)
                .collect(Collectors.toSet());

        Set<String> entityTypes = fragrance.getType().stream()
                .map(Enum::name)
                .collect(Collectors.toSet());

        return isSameParameters(protoTypes, entityTypes);
    }

    private boolean isSameParameters(Set<String> protoParams, Set<String> entityParams) {

        if (protoParams.size() != entityParams.size()) {
            return false;
        }

        return entityParams.stream()
                .allMatch(entityParam -> protoParams.stream()
                        .anyMatch(protoParam -> protoParam.contains(entityParam)));
    }
}
