package com.nd2k.follow_up.baby.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

interface UserBabyJpaRepository extends JpaRepository<UserBabyEntity, Long> {
    boolean existsByUserIdAndBabyId(Long userId, Long babyId);
    List<UserBabyEntity> findByUserId(Long userId);
}