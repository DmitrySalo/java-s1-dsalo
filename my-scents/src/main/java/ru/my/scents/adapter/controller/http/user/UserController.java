package ru.my.scents.adapter.controller.http.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.my.scents.adapter.controller.http.user.converter.RequestUserConverter;
import ru.my.scents.adapter.controller.http.user.converter.ResponseUserConverter;
import ru.my.scents.adapter.controller.http.user.request.CreateUserRequest;
import ru.my.scents.adapter.controller.http.user.request.UpdateUserRequest;
import ru.my.scents.adapter.controller.http.user.response.CreateUserResponse;
import ru.my.scents.adapter.controller.http.user.response.GetUserResponse;
import ru.my.scents.adapter.controller.http.user.response.UpdateUserResponse;
import ru.my.scents.boundary.model.user.CreateUserParam;
import ru.my.scents.boundary.model.user.UpdateUserParam;
import ru.my.scents.boundary.usecase.UserUseCase;
import ru.my.scents.domain.entity.user.User;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserUseCase userUseCase;

    @PostMapping
    public ResponseEntity<CreateUserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        CreateUserParam params = RequestUserConverter.createRequestToModel(request);
        User result = userUseCase.create(params);
        return ResponseEntity.ok(ResponseUserConverter.createResultToResponse(result));
    }

    @PutMapping
    public ResponseEntity<UpdateUserResponse> updateUser(@Valid @RequestBody UpdateUserRequest request) {
        UpdateUserParam params = RequestUserConverter.updateRequestToModel(request);
        User result = userUseCase.update(params);
        return ResponseEntity.ok(ResponseUserConverter.updateResultToResponse(result));
    }

    @GetMapping
    public ResponseEntity<GetUserResponse> getUser(@NotBlank @RequestParam String userId) {
        User result = userUseCase.get(userId);
        return ResponseEntity.ok(ResponseUserConverter.getResultToResponse(result));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteUser(@NotBlank @RequestParam String userId) {
        userUseCase.delete(userId);
        return ResponseEntity.ok().build();
    }
}

