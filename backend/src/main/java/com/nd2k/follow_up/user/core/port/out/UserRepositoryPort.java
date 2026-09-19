package com.nd2k.follow_up.user.core.port.out;

import com.nd2k.follow_up.user.core.domain.User;

import java.util.Optional;

public interface UserRepositoryPort {
    User save(User user);
    Optional<User> findByEmail(String email);
    Optional<User> findById(Long id);
    boolean existsByEmail(String email);
}
