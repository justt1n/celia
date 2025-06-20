package org.celia.auth.infrastructure.adapter.out.security;

import org.celia.auth.application.port.out.RefreshTokenPersistencePort;
import org.celia.auth.domain.entity.RefreshToken;
import org.celia.auth.domain.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class) // K?ch ho?t Mockito
class JwtAdapterTest {

    @Mock
    private RefreshTokenPersistencePort refreshTokenPersistencePort;

    private JwtAdapter jwtAdapter;

    @BeforeEach
    void setUp() {
        jwtAdapter = new JwtAdapter(refreshTokenPersistencePort);
        String testSecret = Base64.getEncoder().encodeToString("a-very-strong-and-long-secret-key-for-testing-purposes".getBytes());
        ReflectionTestUtils.setField(jwtAdapter, "secretKey", testSecret);
        ReflectionTestUtils.setField(jwtAdapter, "accessTokenExpiration", 1000L * 60);
        ReflectionTestUtils.setField(jwtAdapter, "refreshTokenExpiration", 1000L * 60 * 60);
    }

    @Test
    @DisplayName("Create Access Token successfully with correct claims")
    void shouldGenerateAccessTokenWithCorrectClaims() {
        // Arrange
        User user = User.builder().id(1L).email("test@example.com").role("USER").build();

        // Act
        String token = jwtAdapter.generateAccessToken(user);

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertEquals("test@example.com", jwtAdapter.getEmailFromAccessToken(token));
    }

    @Test
    @DisplayName("Create Refresh Token successfully and call save method")
    void shouldGenerateRefreshTokenAndCallSave() {
        // Arrange
        User user = User.builder().id(1L).email("test@example.com").role("USER").build();

        // Act
        String token = jwtAdapter.generateRefreshToken(user);

        // Assert
        assertNotNull(token);
        verify(refreshTokenPersistencePort).save(any(RefreshToken.class));
    }

    @Test
    @DisplayName("Validate Access Token successfully and return true")
    void shouldReturnTrueForValidAccessToken() {
        // Arrange
        User user = User.builder().id(1L).email("test@example.com").role("USER").build();
        String token = jwtAdapter.generateAccessToken(user);

        // Act
        boolean isValid = jwtAdapter.validateAccessToken(token);

        // Assert
        assertTrue(isValid);
    }

    @Test
    @DisplayName("Validate Refresh Token successfully and return true")
    void shouldReturnFalseForExpiredToken() throws InterruptedException {
        // Arrange
        ReflectionTestUtils.setField(jwtAdapter, "accessTokenExpiration", 1L);
        User user = User.builder().id(1L).email("test@example.com").role("USER").build();
        String token = jwtAdapter.generateAccessToken(user);

        Thread.sleep(2);

        // Act
        boolean isValid = jwtAdapter.validateAccessToken(token);

        // Assert
        assertFalse(isValid);
    }
}
