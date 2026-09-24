package com.nd2k.follow_up.user.in.web;

import com.nd2k.follow_up.user.core.port.in.LoginUseCase;
import com.nd2k.follow_up.user.core.port.in.RefreshTokenUseCase;
import com.nd2k.follow_up.user.in.web.dto.AuthResponse;
import com.nd2k.follow_up.user.in.web.dto.LoginRequest;
import com.nd2k.follow_up.user.in.web.dto.RefreshRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/v1")
public class AuthController {

    private final LoginUseCase loginUseCase;
    private final RefreshTokenUseCase refreshTokenUseCase;

    public AuthController(LoginUseCase loginUseCase, RefreshTokenUseCase refreshTokenUseCase) {
        this.loginUseCase = loginUseCase;
        this.refreshTokenUseCase = refreshTokenUseCase;
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return AuthResponse.from(loginUseCase.login(request.email(), request.password()));
    }

    @PostMapping("/refresh")
    public AuthResponse refresh(@Valid @RequestBody RefreshRequest request) {
        return AuthResponse.from(refreshTokenUseCase.refresh(request.refreshToken()));
    }
}
