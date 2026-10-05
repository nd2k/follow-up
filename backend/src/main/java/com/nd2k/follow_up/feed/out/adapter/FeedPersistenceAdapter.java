package com.nd2k.follow_up.feed.out.adapter;

import com.nd2k.follow_up.feed.core.domain.BreastSide;
import com.nd2k.follow_up.feed.core.domain.Feed;
import com.nd2k.follow_up.feed.core.domain.FeedEntry;
import com.nd2k.follow_up.feed.core.port.out.FeedRepositoryPort;
import com.nd2k.follow_up.feed.out.persistence.AggregateFeedEntity;
import com.nd2k.follow_up.feed.out.persistence.AggregateFeedJpaRepository;
import com.nd2k.follow_up.feed.out.persistence.FeedEntryEntity;
import com.nd2k.follow_up.feed.out.persistence.FeedEntryJpaRepository;
import com.nd2k.follow_up.feed.out.persistence.FeedMapper;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class FeedPersistenceAdapter implements FeedRepositoryPort {

    private final AggregateFeedJpaRepository aggregateFeedJpaRepository;
    private final FeedEntryJpaRepository feedEntryJpaRepository;

    FeedPersistenceAdapter(AggregateFeedJpaRepository aggregateFeedJpaRepository,
                           FeedEntryJpaRepository feedEntryJpaRepository) {
        this.aggregateFeedJpaRepository = aggregateFeedJpaRepository;
        this.feedEntryJpaRepository = feedEntryJpaRepository;
    }

    @Override
    public Feed save(Feed feed) {
        AggregateFeedEntity aggregateFeedEntity = aggregateFeedJpaRepository.save(new AggregateFeedEntity(feed.getId(), feed.getBabyId(), feed.getEndTime()));
        List<FeedEntryEntity> savedEntries = feed.getEntries().stream()
                .map(e -> feedEntryJpaRepository.save(FeedMapper.toEntity(e, aggregateFeedEntity.getId(), feed.getBabyId())))
                .toList();
        return toDomain(aggregateFeedEntity, savedEntries);
    }

    private Feed toDomain(AggregateFeedEntity agg, List<FeedEntryEntity> entities) {
        List<FeedEntry> entries = entities.stream()
                .map(e -> FeedEntry.reconstitute(e.getId(), BreastSide.valueOf(e.getBreastSideEntity().name()), e.getStartTime(), e.getAccumulatedInSeconds(), e.getActiveSince(), e.getLastStoppedAt()))
                .toList();
        return Feed.reconstitute(agg.getId(), agg.getBabyId(), entries, agg.getFinishedAt());
    }

    @Override
    public Optional<Feed> findById(Long feedId) {
        Optional<AggregateFeedEntity> aggregateFeedEntity = aggregateFeedJpaRepository.findById(feedId);
        List<FeedEntryEntity> listOfEntries = feedEntryJpaRepository.findByFeedId(feedId);
        return aggregateFeedEntity.map(feedEntity -> FeedMapper.toDomain(feedEntity, listOfEntries));
    }

    @Override
    public Optional<Feed> findMostRecentByBabyId(Long babyId) {
        Optional<FeedEntryEntity> firstByBabyIdOrderByStartTimeDesc = feedEntryJpaRepository.findFirstByBabyIdOrderByStartTimeDesc(babyId);
        if (firstByBabyIdOrderByStartTimeDesc.isPresent()) {
            Optional<AggregateFeedEntity> aggregateFeedEntity = aggregateFeedJpaRepository.findById(firstByBabyIdOrderByStartTimeDesc.get().getFeedId());
            return aggregateFeedEntity.map(feedEntity -> FeedMapper.toDomain(feedEntity, List.of(firstByBabyIdOrderByStartTimeDesc.get())));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public List<Feed> findAllByBabyId(Long babyId) {
        List<AggregateFeedEntity> aggregates = aggregateFeedJpaRepository.findByBabyId(babyId);
        List<Long> ids = aggregates.stream().map(AggregateFeedEntity::getId).toList();
        var entriesByFeed = feedEntryJpaRepository.findByFeedIdIn(ids).stream()
                .collect(java.util.stream.Collectors.groupingBy(FeedEntryEntity::getFeedId));
        return aggregates.stream()
                .map(agg -> toDomain(agg, entriesByFeed.getOrDefault(agg.getId(), List.of())))
                .toList();
    }

    @Override
    public List<Feed> findByBabyIdAndStartTimeBetween(Long babyId, Instant from, Instant to) {
        Map<Long, List<FeedEntryEntity>> entriesByFeedId = new HashMap<>();
        for (FeedEntryEntity entry : feedEntryJpaRepository.findByBabyIdAndStartTimeBetween(babyId, from, to)) {
            entriesByFeedId.computeIfAbsent(entry.getFeedId(), _ -> new ArrayList<>()).add(entry);
        }
        return entriesByFeedId.entrySet().stream()
                .map(entry -> aggregateFeedJpaRepository.findById(entry.getKey())
                        .map(aggregate -> FeedMapper.toDomain(aggregate, entry.getValue())))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteEntry(Long entryId) {
        feedEntryJpaRepository.deleteById(entryId);
    }

    @Override
    public void deleteFeed(Long feedId) {
        aggregateFeedJpaRepository.deleteById(feedId);
    }

    @Override
    public Optional<Feed> findOngoingByBabyId(Long babyId) {
        return aggregateFeedJpaRepository.findFirstByBabyIdAndFinishedAtIsNull(babyId)
                .map(agg -> toDomain(agg, feedEntryJpaRepository.findByFeedId(agg.getId())));
    }
}
