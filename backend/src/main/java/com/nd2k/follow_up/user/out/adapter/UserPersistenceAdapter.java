package com.nd2k.follow_up.user.out.adapter;

import com.nd2k.follow_up.user.core.domain.User;
import com.nd2k.follow_up.user.core.port.out.UserRepositoryPort;
import com.nd2k.follow_up.user.out.persistence.UserJpaRepository;
import com.nd2k.follow_up.user.out.utils.UserMapper;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
class UserPersistenceAdapter implements UserRepositoryPort {

    private final UserJpaRepository userJpaRepository;

    UserPersistenceAdapter(UserJpaRepository userJpaRepository) {
        this.userJpaRepository = userJpaRepository;
    }

    @Override
    public void save(User user) {
        UserMapper.toDomain(userJpaRepository.save(UserMapper.toEntity(user)));

    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userJpaRepository.findByEmail(email).map(UserMapper::toDomain);
    }

    @Override
    public Optional<User> findById(Long id) {
        return userJpaRepository.findById(id).map(UserMapper::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userJpaRepository.existsByEmail(email);
    }
}
