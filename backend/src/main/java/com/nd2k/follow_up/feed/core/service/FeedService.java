package com.nd2k.follow_up.feed.core.service;

import com.nd2k.follow_up.feed.core.domain.BreastSide;
import com.nd2k.follow_up.feed.core.domain.Feed;
import com.nd2k.follow_up.feed.core.domain.FeedEntry;
import com.nd2k.follow_up.feed.core.domain.FeedStats;
import com.nd2k.follow_up.feed.core.domain.exception.FeedEntryNotFoundException;
import com.nd2k.follow_up.feed.core.domain.exception.FeedNotFoundException;
import com.nd2k.follow_up.feed.core.domain.exception.UnauthorizedFeedAccessException;
import com.nd2k.follow_up.feed.core.port.in.AddEntryToFeedUseCase;
import com.nd2k.follow_up.feed.core.port.in.DeleteFeedEntryUseCase;
import com.nd2k.follow_up.feed.core.port.in.EditFeedEntryUseCase;
import com.nd2k.follow_up.feed.core.port.in.FinishedFeedEntryUseCase;
import com.nd2k.follow_up.feed.core.port.in.GetFeedsInRangeUseCase;
import com.nd2k.follow_up.feed.core.port.in.GetStatsUseCase;
import com.nd2k.follow_up.feed.core.port.in.ListFeedsUseCase;
import com.nd2k.follow_up.feed.core.port.in.ModifyExistingFeedUseCase;
import com.nd2k.follow_up.feed.core.port.in.PauseFeedEntryUseCase;
import com.nd2k.follow_up.feed.core.port.in.RecordManualFeedUseCase;
import com.nd2k.follow_up.feed.core.port.in.ResumeFeedEntryUseCase;
import com.nd2k.follow_up.feed.core.port.in.StartFeedEntryUseCase;
import com.nd2k.follow_up.feed.core.port.out.BabyAccessCheckPort;
import com.nd2k.follow_up.feed.core.port.out.FeedRepositoryPort;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class FeedService implements StartFeedEntryUseCase,
        GetStatsUseCase,
        DeleteFeedEntryUseCase,
        GetFeedsInRangeUseCase,
        ListFeedsUseCase,
        ModifyExistingFeedUseCase,
        RecordManualFeedUseCase,
        PauseFeedEntryUseCase,
        ResumeFeedEntryUseCase,
        FinishedFeedEntryUseCase,
        EditFeedEntryUseCase,
        AddEntryToFeedUseCase {

    private static final long ATTACH_GAP_MINUTES = 10;

    private final FeedRepositoryPort feedRepositoryPort;
    private final BabyAccessCheckPort babyAccessCheckPort;
    private final ZoneId zoneId = ZoneId.of("Europe/Brussels");

    public FeedService(FeedRepositoryPort feedRepositoryPort,
                       BabyAccessCheckPort babyAccessCheckPort) {
        this.feedRepositoryPort = feedRepositoryPort;
        this.babyAccessCheckPort = babyAccessCheckPort;
    }

    private void checkAccess(Long userId, Long babyId) {
        if (!babyAccessCheckPort.hasAccess(userId, babyId)) throw new UnauthorizedFeedAccessException(babyId);
    }

    private Instant clampToNow(Instant instant) {
        Instant now = Instant.now();
        return instant.isAfter(now) ? now : instant;
    }

    @Override
    public void deleteEntry(Long babyId, Long requestingUserId, Long feedId, Long entryId) {
        checkAccess(requestingUserId, babyId);
        feedRepositoryPort.deleteEntry(entryId);
        feedRepositoryPort.findById(feedId)
                .map(Feed::getEntries)
                .filter(List::isEmpty)
                .ifPresent(_ -> feedRepositoryPort.deleteFeed(feedId));
    }

    @Override
    public List<Feed> getForBabyInRange(Long babyId, Long requestingUserId, Instant from, Instant to) {
        checkAccess(requestingUserId, babyId);
        return feedRepositoryPort.findByBabyIdAndStartTimeBetween(babyId, from, to).stream()
                .sorted(Comparator.comparing(Feed::getStartTime))
                .toList();
    }

    @Override
    public FeedStats getStats(Long babyId, Long requestingUserId, LocalDate day) {
        checkAccess(requestingUserId, babyId);
        Instant startOfDay = day.atStartOfDay(zoneId).toInstant();
        Instant endOfDay = day.plusDays(1).atStartOfDay(zoneId).toInstant();
        List<Feed> feedsOfDay = feedRepositoryPort.findByBabyIdAndStartTimeBetween(
                babyId,
                startOfDay,
                endOfDay);
        int count = feedsOfDay.size();
        long totalMinutes = (feedsOfDay.stream()
                .mapToLong(Feed::totalDurationInSeconds)
                .sum()) / 60;
        long averageInMinutes = count > 0
                ? totalMinutes / count
                : 0;
        return new FeedStats(count, totalMinutes, averageInMinutes);
    }

    @Override
    public List<Feed> listForBaby(Long babyId, Long requestingUserId) {
        checkAccess(requestingUserId, babyId);
        return feedRepositoryPort.findAllByBabyId(babyId).stream()
                .sorted(Comparator.comparing(Feed::getStartTime).reversed())
                .toList();
    }

    @Override
    public Feed modifyExistingFeed(List<FeedEntry> entries) {
        return null;
    }

    @Override
    public Feed record(Long babyId, Long requestingUserId, List<SideEntry> entries) {
        checkAccess(requestingUserId, babyId);
        if (entries.isEmpty()) throw new IllegalArgumentException("Au moins un côté doit être renseigné");
        Instant now = Instant.now();
        List<FeedEntry> feedEntries = entries.stream()
                .map(entry -> {
                    if (entry.startTime().isAfter(now)) throw new IllegalArgumentException("L'heure de début ne peut pas être dans le futur");
                    if (entry.endTime().isAfter(now)) throw new IllegalArgumentException("L'heure de fin ne peut pas être dans le futur");
                    return FeedEntry.startAndStop(entry.breastSide(), entry.startTime(), entry.endTime());
                })
                .toList();
        Feed feed = Feed.create(babyId, feedEntries.getFirst()).withEntries(feedEntries);
        return feedRepositoryPort.save(feed);
    }

    @Override
    public Feed startEntry(Long babyId, Long requestingUserId, BreastSide breastSide, Instant clientStartTime) {
        checkAccess(requestingUserId, babyId);
        Instant startTime = clampToNow(clientStartTime != null
                ? clientStartTime
                : Instant.now());
        FeedEntry newEntry = FeedEntry.start(breastSide, startTime);
        Feed targetEntry = feedRepositoryPort.findMostRecentByBabyId(babyId)
                .filter(Feed::isOngoing)
                .filter(lastFeed -> {
                    Instant lastActivity = lastFeed.isOngoing() ? lastFeed.getStartTime() : lastFeed.getEndTime();
                    return Duration.between(lastActivity, startTime).toMinutes() <= ATTACH_GAP_MINUTES;
                })
                .map(existingFeed -> {
                    List<FeedEntry> mergedFeedEntries = new ArrayList<>(existingFeed.getEntries());
                    mergedFeedEntries.add(newEntry);
                    return existingFeed.withEntries(mergedFeedEntries);
                })
                .orElseGet(() -> Feed.create(babyId, newEntry));
        return feedRepositoryPort.save(targetEntry);
    }

    @Override
    public Feed pauseEntry(Long babyId, Long requestingUserId, Long feedId, Long entryId, Instant clientPauseTime) {
        checkAccess(requestingUserId, babyId);
        Feed ongoingFeed = getFeedById(babyId, feedId);
        Instant pauseTime = clampToNow(clientPauseTime != null ?
                clientPauseTime :
                Instant.now());
        List<FeedEntry> updatedFeedEntries = ongoingFeed.getEntries().stream()
                .map(entry -> entry.getId().equals(entryId) ?
                        entry.pause(pauseTime) :
                        entry)
                .toList();
        if (updatedFeedEntries.stream().noneMatch(e -> e.getId().equals(entryId))) throw new FeedEntryNotFoundException(entryId);
        return feedRepositoryPort.save(ongoingFeed.withEntries(updatedFeedEntries));
    }

    @Override
    public Feed resumeEntry(Long babyId, Long requestingUserId, Long feedId, Long entryId, Instant clientResumeTime) {
        checkAccess(requestingUserId, babyId);
        Feed ongpingFeed = getFeedById(babyId, feedId);
        Instant resumeTime = clampToNow(clientResumeTime != null ?
                clientResumeTime :
                Instant.now());
        List<FeedEntry> updatedFeedEntries = ongpingFeed.getEntries().stream()
                .map(entry -> entry.getId().equals(entryId) ?
                        entry.resume(resumeTime) :
                        entry)
                .toList();
        if (updatedFeedEntries.stream().noneMatch(e -> e.getId().equals(entryId))) throw new FeedEntryNotFoundException(entryId);
        return feedRepositoryPort.save(ongpingFeed.withEntries(updatedFeedEntries));
    }

    @Override
    public Feed finish(Long babyId, Long requestingUserId, Long feedId, Instant clientFinishedTime) {
        checkAccess(requestingUserId, babyId);
        Feed feed = getFeedById(babyId, feedId);
        Instant finishedTime = clampToNow(clientFinishedTime != null ? clientFinishedTime : Instant.now());
        return feedRepositoryPort.save(feed.finish(finishedTime));
    }

    @Override
    public Feed editEntry(Long babyId, Long requestingUserId, Long feedId, Long entryId, Instant newStartTime, Instant newEndTime) {
        checkAccess(requestingUserId, babyId);
        Feed feed = getFeedById(babyId, feedId);
        Instant now = Instant.now();
        if (newStartTime.isAfter(now) || newEndTime.isAfter(now)) {
            throw new IllegalArgumentException("Les heures ne peuvent pas être dans le futur");
        }
        List<FeedEntry> updatedEntryList = feed.getEntries().stream()
                .map(entry -> entry.getId().equals(entryId) ?
                        entry.editTime(newStartTime, newEndTime) :
                        entry)
                .toList();
        if (updatedEntryList.stream().noneMatch(e -> e.getId().equals(entryId))) {
            throw new FeedEntryNotFoundException(entryId);
        }
        return feedRepositoryPort.save(feed.withEntries(updatedEntryList));
    }

    private @NonNull Feed getFeedById(Long babyId, Long feedId) {
        return feedRepositoryPort.findById(feedId)
                .filter(f -> f.getBabyId().equals(babyId))
                .orElseThrow(() -> new FeedNotFoundException(feedId));
    }

    @Override
    public Feed addEntry(Long babyId, Long requestingUserId, Long feedId, BreastSide breastSide, Instant startTime, Instant endTime) {
        checkAccess(requestingUserId, babyId);
        Feed feed = getFeedById(babyId, feedId);
        if (feed.getEntries().stream().anyMatch(e -> e.getBreastSide() == breastSide)) {
            throw new IllegalArgumentException("Ce côté existe déjà pour cette tétée");
        }
        Instant now = Instant.now();
        if (startTime.isAfter(now) || endTime.isAfter(now)) {
            throw new IllegalArgumentException("Les heures ne peuvent pas être dans le futur");
        }
        FeedEntry newEntry = FeedEntry.startAndStop(breastSide, startTime, endTime);
        List<FeedEntry> updated = new ArrayList<>(feed.getEntries());
        updated.add(newEntry);
        return feedRepositoryPort.save(feed.withEntries(updated));
    }
}
