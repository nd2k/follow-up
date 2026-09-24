package com.nd2k.follow_up.user.in.web.dto;

import com.nd2k.follow_up.user.core.domain.AuthTokens;

public record AuthResponse(String accessToken, String refreshToken) {
    public static AuthResponse from(AuthTokens tokens) {
        return new AuthResponse(tokens.accessToken(), tokens.refreshToken());
    }
}
