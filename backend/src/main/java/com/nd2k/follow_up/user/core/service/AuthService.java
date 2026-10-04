package com.nd2k.follow_up.user.core.service;

import com.nd2k.follow_up.user.core.domain.User;
import com.nd2k.follow_up.user.core.domain.AuthTokens;
import com.nd2k.follow_up.user.core.domain.exception.InvalidCredentialsException;
import com.nd2k.follow_up.user.core.domain.exception.InvalidTokenException;
import com.nd2k.follow_up.user.core.port.in.LoginUseCase;
import com.nd2k.follow_up.user.core.port.in.RefreshTokenUseCase;
import com.nd2k.follow_up.user.core.port.out.PasswordHasherPort;
import com.nd2k.follow_up.user.core.port.out.TokenPort;
import com.nd2k.follow_up.user.core.port.out.UserRepositoryPort;
import org.springframework.stereotype.Service;

@Service
public class AuthService implements LoginUseCase, RefreshTokenUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final PasswordHasherPort passwordHasherPort;
    private final TokenPort tokenPort;

    public AuthService(UserRepositoryPort userRepositoryPort,
                       PasswordHasherPort passwordHasherPort,
                       TokenPort tokenPort) {
        this.userRepositoryPort = userRepositoryPort;
        this.passwordHasherPort = passwordHasherPort;
        this.tokenPort = tokenPort;
    }

    @Override
    public AuthTokens login(String email, String password) {
        User user = userRepositoryPort.findByEmail(email)
                .orElseThrow(InvalidCredentialsException::new);
        if (!passwordHasherPort.matches(password, user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }
        return new AuthTokens(
                tokenPort.generateAccessToken(user.getId(), user.getEmail()),
                tokenPort.generateRefreshToken(user.getId()),
                user.getName()
        );
    }

    @Override
    public AuthTokens refresh(String refreshToken) {
        Long userId = tokenPort.extractUserId(refreshToken);
        User user = userRepositoryPort.findById(userId)
                .orElseThrow(() -> new InvalidTokenException("Utilisateur introuvable"));
        return new AuthTokens(
                tokenPort.generateAccessToken(userId, user.getEmail()),
                tokenPort.generateRefreshToken(userId),
                user.getName()
        );
    }
}
