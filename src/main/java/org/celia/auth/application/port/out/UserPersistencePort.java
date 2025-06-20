package org.celia.auth.application.port.out;

import org.celia.auth.domain.entity.User;

import java.util.Optional;

public interface UserPersistencePort {

    User save(User user);

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

}
