package org.celia.auth.infrastructure.adapter.out.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import org.celia.auth.application.port.out.RefreshTokenPersistencePort;
import org.celia.auth.application.port.out.TokenPort;
import org.celia.auth.domain.entity.RefreshToken;
import org.celia.auth.domain.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class JwtAdapter implements TokenPort {

    private static final Logger logger = LoggerFactory.getLogger(JwtAdapter.class);

    // --- DEPENDENCIES ---
    private final RefreshTokenPersistencePort refreshTokenPersistencePort;

    // --- CONFIGURATION PROPERTIES ---
    @Value("${application.security.jwt.secret-key}")
    private String secretKey;

    @Value("${application.security.jwt.expiration}")
    private long accessTokenExpiration;

    @Value("${application.security.jwt.refresh-token.expiration}")
    private long refreshTokenExpiration;

    // --- PUBLIC METHODS (IMPLEMENTING TOKENPORT) ---

    @Override
    public String generateAccessToken(User user) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("role", user.getRole());
        extraClaims.put("userId", user.getId());
        return buildToken(extraClaims, user.getEmail(), accessTokenExpiration);
    }

    @Override
    public String generateRefreshToken(User user) {
        String tokenString = buildToken(new HashMap<>(), user.getEmail(), refreshTokenExpiration);

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(tokenString)
                .expiryDate(new Date(System.currentTimeMillis() + refreshTokenExpiration).toInstant())
                .revoked(false)
                .build();

        refreshTokenPersistencePort.save(refreshToken);

        return tokenString;
    }

    @Override
    public boolean validateAccessToken(String token) {
        return isTokenStructurallyValid(token);
    }

    @Override
    public boolean validateRefreshToken(String token) {
        boolean isTokenRevokedInDb = refreshTokenPersistencePort.findByToken(token)
                .map(RefreshToken::isRevoked)
                .orElse(true);

        return isTokenStructurallyValid(token) && !isTokenRevokedInDb;
    }

    @Override
    public String getEmailFromAccessToken(String token) {
        return extractEmail(token);
    }

    @Override
    public String getEmailFromRefreshToken(String token) {
        return extractEmail(token);
    }

    // --- PRIVATE HELPER METHODS ---

    private String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private String buildToken(Map<String, Object> extraClaims, String subject, long expiration) {
        return Jwts.builder()
                .claims(extraClaims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey())
                .compact();
    }

    private boolean isTokenStructurallyValid(String token) {
        try {
            extractAllClaims(token);
            return true;
        } catch (SignatureException e) {
            logger.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}