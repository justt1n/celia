package org.celia.auth.infrastructure.persistence.mapper;

import org.celia.auth.domain.entity.RefreshToken;
import org.celia.auth.infrastructure.persistence.entity.RefreshTokenJpaEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = UserMapper.class)
public interface RefreshTokenMapper {

    RefreshToken toDomain(RefreshTokenJpaEntity refreshTokenJpaEntity);

    RefreshTokenJpaEntity toJpaEntity(RefreshToken refreshToken);

}