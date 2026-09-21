package com.nd2k.follow_up.feed.core.service;

import com.nd2k.follow_up.feed.core.domain.*;
import com.nd2k.follow_up.feed.core.port.in.*;
import com.nd2k.follow_up.feed.core.port.out.BabyAccessCheckPort;
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
    private final BabyAccessCheckPort babyAccessCheck;
    private final ZoneId zoneId;

    public FeedService(FeedRepositoryPort feedRepositoryPort, BabyAccessCheckPort babyAccessCheck) {
        this.feedRepositoryPort = feedRepositoryPort;
        this.babyAccessCheck = babyAccessCheck;
        this.zoneId = ZoneId.of("Europe/Brussels");
    }

    private void checkAccess(Long userId, Long babyId) {
        if (!babyAccessCheck.hasAccess(userId, babyId)) {
            throw new UnauthorizedFeedAccessException(babyId);
        }
    }

    @Override
    public Feed startFeed(Long babyId, Long requestingUserId, BreastSide breastSide, Instant clientStartTime) {
        checkAccess(requestingUserId, babyId);
        Instant effectiveStartTime = clientStartTime != null ? clientStartTime : Instant.now();
        return feedRepositoryPort.save(Feed.startFeed(babyId, breastSide, effectiveStartTime));
    }

    @Override
    public Feed stopFeed(Long babyId, Long requestingUserId, Long feedId) {
        checkAccess(requestingUserId, babyId);
        Feed feed = feedRepositoryPort.findById(feedId)
                .filter(f -> f.getBabyId().equals(babyId))
                .orElseThrow(() -> new FeedNotFoundException(feedId));
        return feedRepositoryPort.save(feed.stopFeed(Instant.now()));
    }

    @Override
    public List<Feed> listAllFeed(Long babyId, Long requestingUserId) {
        checkAccess(requestingUserId, babyId);
        return feedRepositoryPort.findAllByBabyId(babyId).stream()
                .sorted(Comparator.comparing(Feed::getStartTime).reversed())
                .toList();
    }

    @Override
    public void deleteFeed(Long babyId, Long requestingUserId, Long feedId) {
        checkAccess(requestingUserId, babyId);
        Feed feed = feedRepositoryPort.findById(feedId)
                .filter(f -> f.getBabyId().equals(babyId))
                .orElseThrow(() -> new FeedNotFoundException(feedId));
        feedRepositoryPort.deleteById(feed.getId());
    }

    @Override
    public FeedStats getStats(Long babyId, Long requestingUserId, LocalDate localDate) {
        checkAccess(requestingUserId, babyId);
        Instant startOfDay = localDate.atStartOfDay(zoneId).toInstant();
        Instant endOfDay = localDate.plusDays(1).atStartOfDay(zoneId).toInstant();
        List<Feed> feedsOfDay = feedRepositoryPort.findByStartTimeBetween(babyId, startOfDay, endOfDay).stream()
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
