package org.celia.auth.domain.usecase;

import org.celia.auth.domain.entity.User;

public interface RegisterUserUseCase {

    User registerUser(RegisterUserCommand command);

    record RegisterUserCommand(String email, String password) {
    }

}
