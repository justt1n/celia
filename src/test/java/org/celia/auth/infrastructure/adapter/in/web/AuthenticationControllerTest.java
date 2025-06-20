package org.celia.auth.infrastructure.adapter.in.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.celia.auth.application.exception.InvalidCredentialsException;
import org.celia.auth.application.port.in.web.dto.LoginRequest;
import org.celia.auth.application.port.in.web.dto.RegistrationRequest;
import org.celia.auth.domain.entity.User;
import org.celia.auth.domain.usecase.LoginUserUseCase;
import org.celia.auth.domain.usecase.RegisterUserUseCase;
import org.celia.auth.infrastructure.config.SecurityConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthenticationController.class)
@Import(SecurityConfig.class)
public class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RegisterUserUseCase registerUserUseCase;

    @MockBean
    private LoginUserUseCase loginUserUseCase;

    @Test
    @DisplayName("POST /register should register a user successfully -> 201 Created")
    void shouldReturnCreated_whenRegistrationIsSuccessful() throws Exception {
        // --- ARRANGE ---
        var request = new RegistrationRequest("test@example.com", "password123");

        var registeredUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .password("encoded-password")
                .role("USER")
                .build();

        when(registerUserUseCase.registerUser(any(RegisterUserUseCase.RegisterUserCommand.class)))
                .thenReturn(registeredUser);

        // --- ACT & ASSERT ---
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("POST /register - invalid data provided ->  400 Bad Request")
    void shouldReturnBadRequest_whenRegistrationDataIsInvalid() throws Exception {
        // Arrange
        var request = new RegistrationRequest("not-an-email", "123");

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /login - successful login returns 200 OK with tokens")
    void shouldReturnOkAndTokens_whenLoginIsSuccessful() throws Exception {
        // Arrange
        var request = new LoginRequest("test@example.com", "password123");
        var loginResult = new LoginUserUseCase.LoginResult("fake-access-token", "fake-refresh-token");

        given(loginUserUseCase.login(any(LoginUserUseCase.LoginCommand.class))).willReturn(loginResult);

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("fake-access-token"))
                .andExpect(jsonPath("$.refreshToken").value("fake-refresh-token"));
    }

    @Test
    @DisplayName("POST /login - invalid credentials returns 401 Unauthorized")
    void shouldReturnUnauthorized_whenLoginCredentialsAreInvalid() throws Exception {
        // Arrange
        var request = new LoginRequest("test@example.com", "wrongpassword");

        given(loginUserUseCase.login(any(LoginUserUseCase.LoginCommand.class)))
                .willThrow(new InvalidCredentialsException("Email ho?c m?t kh?u kh?ng ch?nh x?c."));

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }
}
