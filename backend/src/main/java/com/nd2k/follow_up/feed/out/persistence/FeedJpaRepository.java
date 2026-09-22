package com.nd2k.follow_up.feed.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

interface FeedJpaRepository extends JpaRepository<FeedEntity, Long> {

    List<FeedEntity> findByBabyId(Long babyId);
    List<FeedEntity> findByBabyIdAndStartTimeBetween(Long babyId, Instant from, Instant to);
    Optional<FeedEntity> findFirstByBabyIdOrderByStartTimeDesc(Long babyId);

}
