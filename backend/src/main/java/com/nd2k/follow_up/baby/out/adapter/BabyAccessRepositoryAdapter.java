package com.nd2k.follow_up.baby.out.adapter;

import com.nd2k.follow_up.baby.core.port.out.BabyAccessRepositoryPort;
import com.nd2k.follow_up.baby.out.persistence.UserBabyEntity;
import com.nd2k.follow_up.baby.out.persistence.UserBabyJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BabyAccessRepositoryAdapter implements BabyAccessRepositoryPort {

    private final UserBabyJpaRepository userBabyJpaRepository;

    public BabyAccessRepositoryAdapter(UserBabyJpaRepository userBabyJpaRepository) {
        this.userBabyJpaRepository = userBabyJpaRepository;
    }

    @Override
    public boolean hasAccess(Long userId, Long babyId) {
        return userBabyJpaRepository.existsByUserIdAndBabyId(userId, babyId);
    }

    @Override
    public void link(Long userId, Long babyId) {

    }

    @Override
    public List<Long> findBabyIdsForUser(Long userId) {
        return userBabyJpaRepository.findByUserId(userId).stream()
                .map(UserBabyEntity::getBabyId)
                .toList();
    }
}
