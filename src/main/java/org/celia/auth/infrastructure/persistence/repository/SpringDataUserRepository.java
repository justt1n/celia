package org.celia.auth.infrastructure.persistence.repository;

import org.celia.auth.infrastructure.persistence.entity.UserJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpringDataUserRepository extends JpaRepository<UserJpaEntity, Long> {

    boolean existsByEmail(String email);

    Optional<UserJpaEntity> findByEmail(String email);

}
