package org.celia.auth.application.port.in.web.dto;

public record LoginResponse(

        String accessToken,

        String refreshToken

) {}
