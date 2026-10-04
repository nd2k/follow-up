package com.nd2k.follow_up.baby.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BabyJpaRepository extends JpaRepository<BabyEntity, Long> {
    List<BabyEntity> findAllByIdIn(List<Long> ids);
}
