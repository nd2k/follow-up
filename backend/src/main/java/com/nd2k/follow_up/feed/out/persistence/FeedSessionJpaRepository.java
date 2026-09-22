package com.nd2k.follow_up.feed.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

interface FeedSessionJpaRepository extends JpaRepository<FeedSessionEntity, Long> {}