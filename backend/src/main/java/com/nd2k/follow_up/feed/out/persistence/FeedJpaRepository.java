package com.nd2k.follow_up.feed.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

interface FeedJpaRepository extends JpaRepository<FeedEntity, Long> {

    List<FeedEntity> findByStartTimeBetween(Instant from,Instant to);

}
