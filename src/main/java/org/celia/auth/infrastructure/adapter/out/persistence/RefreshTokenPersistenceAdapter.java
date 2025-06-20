package org.celia.auth.infrastructure.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import org.celia.auth.application.port.out.RefreshTokenPersistencePort;
import org.celia.auth.domain.entity.RefreshToken;
import org.celia.auth.infrastructure.persistence.mapper.RefreshTokenMapper;
import org.celia.auth.infrastructure.persistence.repository.SpringDataRefreshTokenRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RefreshTokenPersistenceAdapter implements RefreshTokenPersistencePort {

    private final SpringDataRefreshTokenRepository refreshTokenRepository;
    private final RefreshTokenMapper refreshTokenMapper;

    @Override
    public RefreshToken save(RefreshToken refreshToken) {
        var jpaEntity = refreshTokenMapper.toJpaEntity(refreshToken);
        var savedEntity = refreshTokenRepository.save(jpaEntity);
        return refreshTokenMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token)
                .map(refreshTokenMapper::toDomain);
    }

}