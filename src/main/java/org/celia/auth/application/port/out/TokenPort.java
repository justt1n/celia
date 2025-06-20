package org.celia.auth.application.port.out;

import org.celia.auth.domain.entity.User;

public interface TokenPort {

    String generateAccessToken(User user);

    String generateRefreshToken(User user);

    boolean validateAccessToken(String token);

    boolean validateRefreshToken(String token);

    String getEmailFromAccessToken(String token);

    String getEmailFromRefreshToken(String token);

}