package com.nd2k.follow_up.user.core.port.in;

import com.nd2k.follow_up.user.core.domain.AuthTokens;

public interface LoginUseCase {
    AuthTokens login(String email, String rawPassword);
}
