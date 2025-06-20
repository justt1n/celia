package org.celia.auth.application.service;

import lombok.RequiredArgsConstructor;
import org.celia.auth.application.exception.InvalidCredentialsException;
import org.celia.auth.application.port.out.PasswordEncoderPort;
import org.celia.auth.application.port.out.TokenPort;
import org.celia.auth.application.port.out.UserPersistencePort;
import org.celia.auth.domain.usecase.LoginUserUseCase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LoginUserService implements LoginUserUseCase {

    private final UserPersistencePort userPersistencePort;

    private final PasswordEncoderPort passwordEncoderPort;

    private final TokenPort tokenPort;

    @Override
    @Transactional(readOnly = true)
    public LoginResult login(LoginCommand command) {
        var user = userPersistencePort.findByEmail(command.email())
                .orElseThrow(() -> new InvalidCredentialsException("Wrong email or password."));

        if (!passwordEncoderPort.matches(command.password(), user.getPassword())) {
            throw new InvalidCredentialsException("Wrong email or password.");
        }

        String accessToken = tokenPort.generateAccessToken(user);
        String refreshToken = tokenPort.generateRefreshToken(user);

        return new LoginResult(accessToken, refreshToken);
    }

}
