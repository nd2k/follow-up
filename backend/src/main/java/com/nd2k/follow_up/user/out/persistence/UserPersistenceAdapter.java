package com.nd2k.follow_up.user.out.persistence;

import com.nd2k.follow_up.user.core.domain.User;
import com.nd2k.follow_up.user.core.port.out.UserRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
class UserPersistenceAdapter implements UserRepositoryPort {

    private final UserJpaRepository jpaRepository;

    UserPersistenceAdapter(UserJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public void save(User user) {
        UserMapper.toDomain(jpaRepository.save(UserMapper.toEntity(user)));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpaRepository.findByEmail(email).map(UserMapper::toDomain);
    }

    @Override
    public Optional<User> findById(Long id) {
        return jpaRepository.findById(id).map(UserMapper::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }
}
