package org.celia.auth.application.service;

import lombok.RequiredArgsConstructor;
import org.celia.auth.application.port.out.PasswordEncoderPort;
import org.celia.auth.application.port.out.UserPersistencePort;
import org.celia.auth.domain.entity.User;
import org.celia.auth.domain.usecase.RegisterUserUseCase;
import org.celia.auth.application.exception.EmailAlreadyExistsException;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class RegisterUserService implements RegisterUserUseCase {

    private final UserPersistencePort userPersistencePort;

    private final PasswordEncoderPort passwordEncoderPort;

    @Override
    public User registerUser(RegisterUserCommand command) {
        if (userPersistencePort.existsByEmail(command.email())) {
            throw new EmailAlreadyExistsException("Email already exists: " + command.email());
        }

        User user = User.builder()
                .email(command.email())
                .password(passwordEncoderPort.encode(command.password()))
                .role("USER") // Default role, can be changed as needed
                .build();

        return userPersistencePort.save(user);
    }
}
