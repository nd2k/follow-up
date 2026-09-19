package com.nd2k.follow_up.user.core.port.in;

import com.nd2k.follow_up.user.core.domain.AuthTokens;

public interface RefreshTokenUseCase {
    AuthTokens refresh(String refreshToken);
}