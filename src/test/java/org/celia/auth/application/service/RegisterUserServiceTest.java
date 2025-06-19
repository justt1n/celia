package org.celia.auth.application.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
        // Given
        var command = new RegisterUserUseCase.RegisterUserCommand("test@example.com", "password123");
        when(userPersistencePort.existsByEmail(command.email())).thenReturn(false);
        when(passwordEncoderPort.encode(command.password())).thenReturn("encodedPassword");

        // When
        User registeredUser = registerUserService.registerUser(command);

        // Then
        assertNotNull(registeredUser);
        assertEquals("encodedPassword", registeredUser.getPassword());
        verify(userPersistencePort).save(any(User.class)); // ??m b?o h?m save ???c g?i
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
