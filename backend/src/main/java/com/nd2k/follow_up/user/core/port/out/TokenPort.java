package com.nd2k.follow_up.user.core.port.out;

public interface TokenPort {
    String generateAccessToken(Long userId, String email);
    String generateRefreshToken(Long userId);
    Long extractUserId(String token); // lève InvalidTokenException si invalide/expiré
}
