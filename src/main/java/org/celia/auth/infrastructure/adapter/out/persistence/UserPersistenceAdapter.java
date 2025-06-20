package org.celia.auth.infrastructure.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import org.celia.auth.application.port.out.UserPersistencePort;
import org.celia.auth.domain.entity.User;
import org.celia.auth.infrastructure.persistence.entity.UserJpaEntity;
import org.celia.auth.infrastructure.persistence.mapper.UserMapper;
import org.celia.auth.infrastructure.persistence.repository.SpringDataUserRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserPersistenceAdapter implements UserPersistencePort {

    private final SpringDataUserRepository userRepository;

    private final UserMapper userMapper;

    @Override
    public User save(User user) {
        UserJpaEntity userJpaEntity = userMapper.toJpaEntity(user);

        UserJpaEntity savedUser = userRepository.save(userJpaEntity);

        return userMapper.toDomain(savedUser);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        Optional<UserJpaEntity> optionalUserJpaEntity = userRepository.findByEmail(email);

        return optionalUserJpaEntity.map(userMapper::toDomain);
    }

}
