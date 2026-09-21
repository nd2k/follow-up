package com.nd2k.follow_up.feed.core.service;

import com.nd2k.follow_up.feed.core.domain.BreastSide;
import com.nd2k.follow_up.feed.core.domain.Feed;
import com.nd2k.follow_up.feed.core.domain.FeedNotFoundException;
import com.nd2k.follow_up.feed.core.domain.FeedStats;
import com.nd2k.follow_up.feed.core.port.in.*;
import com.nd2k.follow_up.feed.core.port.out.FeedRepositoryPort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;

@Service
public class FeedService implements
        StartFeedUseCase,
        StopFeedUseCase,
        ListFeedsUseCase,
        DeleteFeedUseCase,
        GetStatsUseCase {

    private final FeedRepositoryPort feedRepositoryPort;
    private final ZoneId zoneId;

    public FeedService(FeedRepositoryPort feedRepositoryPort) {
        this.feedRepositoryPort = feedRepositoryPort;
        this.zoneId = ZoneId.of("Europe/Brussels");
    }

    @Override
    public Feed startFeed(BreastSide breastSide, Instant clientStartTime) {
        Instant effectiveStartTime = clientStartTime != null ? clientStartTime : Instant.now();
        Feed newFeed = Feed.startFeed(breastSide, effectiveStartTime);
        return feedRepositoryPort.save(newFeed);
    }

    @Override
    public Feed stopFeed(Long id) {
        Feed feed = feedRepositoryPort.findById(id)
                .orElseThrow(() -> new FeedNotFoundException(id));
        Feed finishedFeed = feed.stopFeed(Instant.now());
        return feedRepositoryPort.save(finishedFeed);
    }

    @Override
    public List<Feed> listAllFeed() {
        return feedRepositoryPort.findAll().stream()
                .sorted(Comparator.comparing(Feed::getStartTime).reversed())
                .toList();
    }

    @Override
    public void deleteFeed(Long id) {
        feedRepositoryPort.findById(id)
                .orElseThrow(() -> new FeedNotFoundException(id));
        feedRepositoryPort.deleteById(id);
    }

    @Override
    public FeedStats getStats(LocalDate localDate) {
        Instant startOfDay = localDate.atStartOfDay(zoneId).toInstant();
        Instant endOfDay = localDate.plusDays(1).atStartOfDay(zoneId).toInstant();
        List<Feed> feedsOfDay = feedRepositoryPort.findByStartTimeBetween(startOfDay, endOfDay).stream()
                .filter(feed -> !feed.isOngoing())
                .toList();
        int count = feedsOfDay.size();
        long totalSeconds = feedsOfDay.stream()
                .mapToLong(Feed::durationSeconds)
                .sum();
        long totalDurationInMinutes = totalSeconds / 60;
        long averageInMinutes = count > 0 ? totalDurationInMinutes / count : 0;
        return new FeedStats(count, totalDurationInMinutes, averageInMinutes);
    }
}
