package org.celia.auth.infrastructure.adapter.out.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BcryptPasswordEncoderAdapterTest {

    private BcryptPasswordEncoderAdapter passwordEncoderAdapter;

    @BeforeEach
    void setUp() {
        passwordEncoderAdapter = new BcryptPasswordEncoderAdapter();
    }

    @Test
    @DisplayName("Test encoding password successfully")
    void shouldEncodePasswordSuccessfully() {
        String rawPassword = "mySecretPassword123";

        String encodedPassword = passwordEncoderAdapter.encode(rawPassword);

        assertNotNull(encodedPassword);

        assertNotEquals(rawPassword, encodedPassword);

        assertTrue(passwordEncoderAdapter.matches(rawPassword, encodedPassword));
    }

    @Test
    @DisplayName("Test when raw password matches encoded password, it should return true")
    void shouldReturnTrue_whenRawPasswordMatchesEncodedPassword() {
        String rawPassword = "password123";
        String encodedPassword = passwordEncoderAdapter.encode(rawPassword);

        boolean isMatch = passwordEncoderAdapter.matches(rawPassword, encodedPassword);

        assertTrue(isMatch);
    }

    @Test
    @DisplayName("Test when raw password does not match encoded password, it should return false")
    void shouldReturnFalse_whenRawPasswordDoesNotMatchEncodedPassword() {
        String correctPassword = "password123";
        String wrongPassword = "wrongPasswordABC";
        String encodedPassword = passwordEncoderAdapter.encode(correctPassword);

        boolean isMatch = passwordEncoderAdapter.matches(wrongPassword, encodedPassword);

        assertFalse(isMatch);
    }
}
