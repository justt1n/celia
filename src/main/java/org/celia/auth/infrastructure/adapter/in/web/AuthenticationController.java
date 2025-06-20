package org.celia.auth.infrastructure.adapter.in.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.celia.auth.application.port.in.web.dto.LoginRequest;
import org.celia.auth.application.port.in.web.dto.LoginResponse;
import org.celia.auth.application.port.in.web.dto.RegistrationRequest;
import org.celia.auth.domain.usecase.LoginUserUseCase;
import org.celia.auth.domain.usecase.RegisterUserUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final RegisterUserUseCase registerUserUseCase;
    private final LoginUserUseCase loginUserUseCase;

    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody RegistrationRequest request) {
        var command = new RegisterUserUseCase.RegisterUserCommand(request.email(), request.password());
        registerUserUseCase.registerUser(command);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        var command = new LoginUserUseCase.LoginCommand(request.email(), request.password());

        LoginUserUseCase.LoginResult loginResult = loginUserUseCase.login(command);

        LoginResponse response = new LoginResponse(loginResult.accessToken(), loginResult.refreshToken());

        return ResponseEntity.ok(response);
    }

}