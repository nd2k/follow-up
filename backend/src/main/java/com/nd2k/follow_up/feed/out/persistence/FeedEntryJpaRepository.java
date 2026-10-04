package com.nd2k.follow_up.feed.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface FeedEntryJpaRepository extends JpaRepository<FeedEntryEntity, Long> {

    List<FeedEntryEntity> findByFeedId(Long feedId);
    List<FeedEntryEntity> findByFeedIdIn(List<Long> feedIds);
    Optional<FeedEntryEntity> findFirstByBabyIdOrderByStartTimeDesc(Long babyId);
    List<FeedEntryEntity> findByBabyIdAndStartTimeBetween(Long babyId, Instant from, Instant to);
}
