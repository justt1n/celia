package org.celia.auth.application.port.out;

public interface PasswordEncoderPort {

    String encode(String password);

    boolean matches(String rawPassword, String encodedPassword);

}
