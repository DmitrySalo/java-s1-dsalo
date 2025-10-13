package ru.my.scents.adapter.controller.http.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.my.scents.adapter.controller.http.user.convertor.RequestUserConvertor;
import ru.my.scents.adapter.controller.http.user.convertor.ResponseUserConvertor;
import ru.my.scents.adapter.controller.http.user.request.CreateUserRequest;
import ru.my.scents.adapter.controller.http.user.response.CreateUserResponse;
import ru.my.scents.boundary.model.CreateUserParam;
import ru.my.scents.boundary.model.User;
import ru.my.scents.boundary.usecase.CreateUserUseCase;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final CreateUserUseCase createUserUseCase;

    @PostMapping
    public ResponseEntity<CreateUserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        CreateUserParam params = RequestUserConvertor.createRequestToModel(request);
        User result = createUserUseCase.execute(params);
        return ResponseEntity.ok(ResponseUserConvertor.createResultToResponse(result));
    }
}

