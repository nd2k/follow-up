package com.nd2k.follow_up.user.out.adapter;

import com.nd2k.follow_up.user.core.port.out.PasswordHasherPort;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordHasherAdapter implements PasswordHasherPort {

    private final Argon2PasswordEncoder argon2PasswordEncoder =
            new Argon2PasswordEncoder(16, 32, 1, 19456, 2);

    @Override
    public String hash(String rawPassword) {
        return argon2PasswordEncoder.encode(rawPassword);
    }

    @Override
    public boolean matches(String rawPassword, String hash) {
        return argon2PasswordEncoder.matches(rawPassword, hash);
    }
}
