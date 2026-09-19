package com.nd2k.follow_up.config;

import com.nd2k.follow_up.user.core.domain.User;
import com.nd2k.follow_up.user.core.port.out.PasswordHasherPort;
import com.nd2k.follow_up.user.core.port.out.UserRepositoryPort;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class UserSeeder implements CommandLineRunner {

    private final UserRepositoryPort userRepository;
    private final PasswordHasherPort passwordHasher;

    @Value("${app.seed.user1-email}") private String user1Email;
    @Value("${app.seed.user1-password}") private String user1Password;
    @Value("${app.seed.user1-name}") private String user1Name;
    @Value("${app.seed.user2-email}") private String user2Email;
    @Value("${app.seed.user2-password}") private String user2Password;
    @Value("${app.seed.user2-name}") private String user2Name;

    public UserSeeder(UserRepositoryPort userRepository, PasswordHasherPort passwordHasher) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
    }

    @Override
    public void run(String @NonNull ... args) {
        seedIfAbsent(user1Email, user1Password, user1Name);
        seedIfAbsent(user2Email, user2Password, user2Name);
    }

    private void seedIfAbsent(String email, String rawPassword, String name) {
        if (!userRepository.existsByEmail(email)) {
            userRepository.save(User.create(email, passwordHasher.hash(rawPassword), name));
        }
    }
}
