package org.celia.auth.domain.usecase;

public interface LoginUserUseCase {

    LoginResult login(LoginCommand command);

    record LoginCommand(String email, String password) {}

    record LoginResult(String accessToken, String refreshToken) {}

}
