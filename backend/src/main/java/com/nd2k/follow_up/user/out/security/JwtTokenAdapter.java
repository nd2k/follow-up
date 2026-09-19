package com.nd2k.follow_up.user.out.security;

import com.nd2k.follow_up.user.core.domain.InvalidTokenException;
import com.nd2k.follow_up.user.core.port.out.TokenPort;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;

@Component
class JwtTokenAdapter implements TokenPort {

    private final SecretKey key;
    private static final long ACCESS_TOKEN_TTL_MINUTES = 15;
    private static final long REFRESH_TOKEN_TTL_DAYS = 30;

    JwtTokenAdapter(@Value("${app.security.jwt-secret}") String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    @Override
    public String generateAccessToken(Long userId, String email) {
        return Jwts.builder()
                .subject(userId.toString())
                .claim("email", email)
                .claim("type", "access")
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plusSeconds(ACCESS_TOKEN_TTL_MINUTES * 60)))
                .signWith(key)
                .compact();
    }

    @Override
    public String generateRefreshToken(Long userId) {
        return Jwts.builder()
                .subject(userId.toString())
                .claim("type", "refresh")
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plusSeconds(REFRESH_TOKEN_TTL_DAYS * 24 * 3600)))
                .signWith(key)
                .compact();
    }

    @Override
    public Long extractUserId(String token) {
        try {
            Claims claims = Jwts.parser().verifyWith(key).build()
                    .parseSignedClaims(token)
                    .getPayload();
            return Long.parseLong(claims.getSubject());
        } catch (JwtException | IllegalArgumentException e) {
            throw new InvalidTokenException("Token invalide ou expiré");
        }
    }
}
