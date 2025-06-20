package org.celia.auth.application.port.out;

import org.celia.auth.domain.entity.RefreshToken;

import java.util.Optional;

public interface RefreshTokenPersistencePort {

    RefreshToken save(RefreshToken refreshToken);

    Optional<RefreshToken> findByToken(String token);

}
