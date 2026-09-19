package com.nd2k.follow_up.user.core.service;

import com.nd2k.follow_up.user.core.domain.AuthTokens;
import com.nd2k.follow_up.user.core.domain.InvalidCredentialsException;
import com.nd2k.follow_up.user.core.domain.InvalidTokenException;
import com.nd2k.follow_up.user.core.domain.User;
import com.nd2k.follow_up.user.core.port.in.LoginUseCase;
import com.nd2k.follow_up.user.core.port.in.RefreshTokenUseCase;
import com.nd2k.follow_up.user.core.port.out.PasswordHasherPort;
import com.nd2k.follow_up.user.core.port.out.TokenPort;
import com.nd2k.follow_up.user.core.port.out.UserRepositoryPort;
import org.springframework.stereotype.Service;

@Service
public class AuthService implements LoginUseCase, RefreshTokenUseCase {

    private final UserRepositoryPort userRepository;
    private final PasswordHasherPort passwordHasher;
    private final TokenPort tokenPort;

    public AuthService(UserRepositoryPort userRepository, PasswordHasherPort passwordHasher, TokenPort tokenPort) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
        this.tokenPort = tokenPort;
    }

    @Override
    public AuthTokens login(String email, String rawPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(InvalidCredentialsException::new);
        if (!passwordHasher.matches(rawPassword, user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }
        return new AuthTokens(
                tokenPort.generateAccessToken(user.getId(), user.getEmail()),
                tokenPort.generateRefreshToken(user.getId())
        );
    }

    @Override
    public AuthTokens refresh(String refreshToken) {
        Long userId = tokenPort.extractUserId(refreshToken);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new InvalidTokenException("Utilisateur introuvable"));
        return new AuthTokens(
                tokenPort.generateAccessToken(user.getId(), user.getEmail()),
                tokenPort.generateRefreshToken(user.getId())
        );
    }
}
