package com.nd2k.follow_up.feed.core.service;

import com.nd2k.follow_up.feed.core.domain.*;
import com.nd2k.follow_up.feed.core.port.in.*;
import com.nd2k.follow_up.feed.core.port.out.BabyAccessCheckPort;
import com.nd2k.follow_up.feed.core.port.out.FeedRepositoryPort;
import com.nd2k.follow_up.feed.core.port.out.FeedSessionRepositoryPort;
import org.springframework.stereotype.Service;

import java.time.Duration;
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
        GetStatsUseCase,
        GetFeedsInRangeUseCase,
        RecordManualFeedUseCase {

    private static final long SESSION_GAP_MINUTES = 10;

    private final FeedRepositoryPort feedRepositoryPort;
    private final FeedSessionRepositoryPort sessionRepository;
    private final BabyAccessCheckPort babyAccessCheck;
    private final ZoneId zoneId = ZoneId.of("Europe/Brussels");

    public FeedService(FeedRepositoryPort feedRepositoryPort, FeedSessionRepositoryPort sessionRepository, BabyAccessCheckPort babyAccessCheck) {
        this.feedRepositoryPort = feedRepositoryPort;
        this.sessionRepository = sessionRepository;
        this.babyAccessCheck = babyAccessCheck;
    }

    private void checkAccess(Long userId, Long babyId) {
        if (!babyAccessCheck.hasAccess(userId, babyId)) {
            throw new UnauthorizedFeedAccessException(babyId);
        }
    }

    private Long resolveSessionId(Long babyId, Instant startTime) {
        return feedRepositoryPort.findMostRecentByBabyId(babyId)
                .filter(last -> {
                    Instant lastActivity = last.isOngoing() ? last.getStartTime() : last.getEndTime();
                    return Duration.between(lastActivity, startTime).toMinutes() <= SESSION_GAP_MINUTES;
                })
                .map(Feed::getSessionId)
                .orElseGet(() -> sessionRepository.save(FeedSession.create(babyId)).getId());
    }

    @Override
    public Feed startFeed(Long babyId, Long requestingUserId, BreastSide breastSide, Instant clientStartTime) {
        checkAccess(requestingUserId, babyId);
        Instant effectiveStartTime = clientStartTime != null ? clientStartTime : Instant.now();
        Long sessionId = resolveSessionId(babyId, effectiveStartTime);
        return feedRepositoryPort.save(Feed.startFeed(babyId, sessionId, breastSide, effectiveStartTime));
    }

    @Override
    public Feed stopFeed(Long babyId, Long requestingUserId, Long feedId, Instant clientEndTime) {
        checkAccess(requestingUserId, babyId);
        Feed feed = feedRepositoryPort.findById(feedId)
                .filter(f -> f.getBabyId().equals(babyId))
                .orElseThrow(() -> new FeedNotFoundException(feedId));
        Instant effectiveEndTime = clientEndTime != null ? clientEndTime : Instant.now();
        return feedRepositoryPort.save(feed.stopFeed(effectiveEndTime));
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
        List<Feed> feedsOfDay = feedRepositoryPort.findByBabyIdAndStartTimeBetween(babyId, startOfDay, endOfDay).stream()
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

    @Override
    public List<Feed> getForBabyInRange(Long babyId, Long requestingUserId, Instant from, Instant to) {
        checkAccess(requestingUserId, babyId);
        return feedRepositoryPort.findByBabyIdAndStartTimeBetween(babyId, from, to).stream()
                .sorted(Comparator.comparing(Feed::getStartTime))
                .toList();
    }

    @Override
    public List<Feed> record(Long babyId, Long requestingUserId, List<SideEntry> entries) {
        checkAccess(requestingUserId, babyId);
        if (entries.isEmpty()) {
            throw new IllegalArgumentException("Au moins un côté doit être renseigné");
        }
        Instant now = Instant.now();
        for (RecordManualFeedUseCase.SideEntry entry : entries) {
            if (!entry.endTime().isAfter(entry.startTime())) {
                throw new IllegalArgumentException("L'heure de fin doit être après l'heure de début");
            }
            if (entry.startTime().isAfter(now)) {
                throw new IllegalArgumentException("L'heure de début ne peut pas être dans le futur");
            }
        }
        Long sessionId = sessionRepository.save(FeedSession.create(babyId)).getId();
        return entries.stream()
                .map(entry -> feedRepositoryPort.save(
                        Feed.startFeed(babyId, sessionId, entry.breastSide(), entry.startTime()).stopFeed(entry.endTime())
                ))
                .toList();
    }
}
