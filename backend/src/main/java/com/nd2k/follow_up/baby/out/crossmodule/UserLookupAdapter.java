package com.nd2k.follow_up.baby.out.crossmodule;

import com.nd2k.follow_up.baby.core.port.out.UserLookupPort;
import com.nd2k.follow_up.user.core.domain.User;
import com.nd2k.follow_up.user.core.port.out.UserRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
class UserLookupAdapter implements UserLookupPort {

    private final UserRepositoryPort userRepository;

    UserLookupAdapter(UserRepositoryPort userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<Long> findUserIdByEmail(String email) {
        return userRepository.findByEmail(email).map(User::getId);
    }
}
