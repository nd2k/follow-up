package com.nd2k.follow_up.feed.out.persistence;

import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AggregateFeedJpaRepository extends JpaRepository<AggregateFeedEntity, Long> {

    List<AggregateFeedEntity> findByBabyId(Long babyId);
    Optional<AggregateFeedEntity> findById(@NonNull Long feedId);
}
