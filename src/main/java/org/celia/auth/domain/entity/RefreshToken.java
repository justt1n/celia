package org.celia.auth.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {

    private Long id;

    private String token;

    private Instant expiryDate;

    private boolean revoked;

    private User user;

}
