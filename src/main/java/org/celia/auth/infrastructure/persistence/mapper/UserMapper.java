package org.celia.auth.infrastructure.persistence.mapper;

import org.celia.auth.domain.entity.User;
import org.celia.auth.infrastructure.persistence.entity.UserJpaEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toDomain(UserJpaEntity userJpaEntity);

    UserJpaEntity toJpaEntity(User user);

}
