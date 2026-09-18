package com.nd2k.follow_up.feed.out.persistence;

import com.nd2k.follow_up.feed.core.domain.Feed;
import com.nd2k.follow_up.feed.core.port.out.FeedRepositoryPort;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Component
class FeedPersistenceAdapter implements FeedRepositoryPort {

    private final FeedJpaRepository feedJpaRepository;

    public FeedPersistenceAdapter(FeedJpaRepository feedJpaRepository) {
        this.feedJpaRepository = feedJpaRepository;
    }

    @Override
    public Feed save(Feed feed) {
        FeedEntity feedEntitySaved = feedJpaRepository.save(FeedMapper.toEntity(feed));
        return FeedMapper.toDomain(feedEntitySaved);
    }

    @Override
    public Optional<Feed> findById(Long id) {
        return feedJpaRepository.findById(id).map(FeedMapper::toDomain);
    }

    @Override
    public List<Feed> findAll() {
        return feedJpaRepository.findAll().stream().map(FeedMapper::toDomain).toList();
    }

    @Override
    public List<Feed> findByStartTimeBetween(Instant from, Instant to) {
        return feedJpaRepository.findByStartTimeBetween(from, to).stream()
                .map(FeedMapper::toDomain).toList();
    }

    @Override
    public void deleteById(Long id) {
        feedJpaRepository.deleteById(id);
    }
}
