package ru.my.scents.adapter.controller.http.fragrance;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.my.scents.adapter.controller.http.fragrance.converter.RequestFragranceConverter;
import ru.my.scents.adapter.controller.http.fragrance.converter.ResponseFragranceConverter;
import ru.my.scents.adapter.controller.http.fragrance.request.CreateFragranceRequest;
import ru.my.scents.adapter.controller.http.fragrance.request.UpdateFragranceRequest;
import ru.my.scents.adapter.controller.http.fragrance.response.CreateFragranceResponse;
import ru.my.scents.adapter.controller.http.fragrance.response.GetFragranceResponse;
import ru.my.scents.adapter.controller.http.fragrance.response.UpdateFragranceResponse;
import ru.my.scents.boundary.model.fragrance.CreateFragranceParam;
import ru.my.scents.boundary.model.fragrance.UpdateFragranceParam;
import ru.my.scents.boundary.usecase.FragranceUseCase;
import ru.my.scents.domain.entity.fragrance.Fragrance;

@Validated
@RestController
@RequestMapping("/api/v1/fragrances")
@RequiredArgsConstructor
public class FragranceController {

    private final FragranceUseCase fragranceUseCase;

    @PostMapping
    public ResponseEntity<CreateFragranceResponse> createFragrance(@Valid @RequestBody CreateFragranceRequest request) {
        CreateFragranceParam params = RequestFragranceConverter.createRequestToModel(request);
        Fragrance result = fragranceUseCase.create(params);
        return ResponseEntity.ok(ResponseFragranceConverter.createResultToResponse(result));
    }

    @PutMapping
    public ResponseEntity<UpdateFragranceResponse> updateFragrance(@Valid @RequestBody UpdateFragranceRequest request) {
        UpdateFragranceParam params = RequestFragranceConverter.updateRequestToModel(request);
        Fragrance result = fragranceUseCase.update(params);
        return ResponseEntity.ok(ResponseFragranceConverter.updateResultToResponse(result));
    }

    @GetMapping
    public ResponseEntity<GetFragranceResponse> getFragrance(@NotBlank @RequestParam String fragranceId) {
        Fragrance result = fragranceUseCase.get(fragranceId);
        return ResponseEntity.ok(ResponseFragranceConverter.getResultToResponse(result));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteFragrance(@NotBlank @RequestParam String fragranceId) {
        fragranceUseCase.delete(fragranceId);
        return ResponseEntity.ok().build();
    }
}

