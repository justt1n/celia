package org.celia.auth.application.service;

import org.celia.auth.application.exception.EmailAlreadyExistsException;
import org.celia.auth.application.port.out.PasswordEncoderPort;
import org.celia.auth.application.port.out.UserPersistencePort;
import org.celia.auth.domain.entity.User;
import org.celia.auth.domain.usecase.RegisterUserUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RegisterUserServiceTest {

    @Mock
    private UserPersistencePort userPersistencePort;

    @Mock
    private PasswordEncoderPort passwordEncoderPort;

    @InjectMocks
    private RegisterUserService registerUserService;

    @Test
    void shouldRegisterUserSuccessfully_whenEmailIsUnique() {
        var command = new RegisterUserUseCase.RegisterUserCommand("test@example.com", "password123");

        when(userPersistencePort.existsByEmail(command.email())).thenReturn(false);
        when(passwordEncoderPort.encode(command.password())).thenReturn("encodedPassword");

        User savedUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .password("encodedPassword")
                .role("USER")
                .build();

        when(userPersistencePort.save(any(User.class))).thenReturn(savedUser);

        User registeredUser = registerUserService.registerUser(command);

        assertNotNull(registeredUser);

        assertEquals(1L, registeredUser.getId());
        assertEquals("test@example.com", registeredUser.getEmail());
        verify(userPersistencePort).save(any(User.class));
    }


    @Test
    void shouldThrowException_whenEmailAlreadyExists() {
        // Given
        var command = new RegisterUserUseCase.RegisterUserCommand("test@example.com", "password123");
        when(userPersistencePort.existsByEmail(command.email())).thenReturn(true);

        // When & Then
        assertThrows(EmailAlreadyExistsException.class, () -> {
            registerUserService.registerUser(command);
        });
        verify(userPersistencePort, never()).save(any(User.class));
    }
}
