package com.nd2k.follow_up.baby.out.persistence;

import com.nd2k.follow_up.baby.core.port.out.BabyAccessRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
class BabyAccessPersistenceAdapter implements BabyAccessRepositoryPort {

    private final UserBabyJpaRepository jpaRepository;

    BabyAccessPersistenceAdapter(UserBabyJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public void link(Long userId, Long babyId) {
        if (!jpaRepository.existsByUserIdAndBabyId(userId, babyId)) {
            jpaRepository.save(new UserBabyEntity(userId, babyId));
        }
    }

    @Override
    public boolean hasAccess(Long userId, Long babyId) {
        return jpaRepository.existsByUserIdAndBabyId(userId, babyId);
    }

    @Override
    public List<Long> findBabyIdsForUser(Long userId) {
        return jpaRepository.findByUserId(userId).stream()
                .map(UserBabyEntity::getBabyId)
                .toList();
    }
}
