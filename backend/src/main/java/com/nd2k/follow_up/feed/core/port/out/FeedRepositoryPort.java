package com.nd2k.follow_up.feed.core.port.out;

import com.nd2k.follow_up.feed.core.domain.Feed;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface FeedRepositoryPort {

    Feed save(Feed feed);
    Optional<Feed> findById(Long id);
    List<Feed> findAll();
    List<Feed> findByStartTimeBetween(Instant from, Instant to);
    void deleteById(Long id);
}
