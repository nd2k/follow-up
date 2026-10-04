package com.nd2k.follow_up.feed.core.domain;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Feed {

    private final Long id;
    private final Long babyId;
    private final List<FeedEntry> entries;
    private final Instant finishedAt;

    public Feed(Long id, Long babyId, List<FeedEntry> entries, Instant finishedAt) {
        this.id = id;
        this.babyId = babyId;
        this.entries = List.copyOf(entries);
        this.finishedAt = finishedAt;
    }

    public static Feed create(Long babyId, FeedEntry firstEntry) {
        Objects.requireNonNull(babyId);
        return new Feed(null, babyId, List.of(firstEntry), null);
    }

    public Feed withEntries(List<FeedEntry> newEntries) {
        return new Feed(id, babyId, newEntries, finishedAt);
    }

    public boolean isOngoing() {
        return finishedAt == null;
    }

    public Instant getStartTime() {
        return entries.stream().map(FeedEntry::getStartTime).min(Instant::compareTo)
                .orElseThrow(() -> new IllegalStateException("Une tétée doit avoir au moins une entrée"));
    }

    public Instant getEndTime() {
        if (isOngoing()) return null;
        return entries.stream().map(FeedEntry::getLastStoppedAt).max(Instant::compareTo).orElseThrow();
    }

    public long totalDurationInSeconds() {
        return entries.stream().mapToLong(FeedEntry::durationInSeconds).sum();
    }

    public static Feed reconstitute(Long id, Long babyId, List<FeedEntry> entries, Instant finishedAt) {
        return new Feed(id, babyId, entries, finishedAt);
    }

    public Feed finish(Instant finishTime) {
        if (finishedAt != null) throw new IllegalStateException("Cette tétée est déjà terminée");
        List<FeedEntry> finishedFeedEntryList = new ArrayList<>();
        for (FeedEntry feedEntry: entries) {
            finishedFeedEntryList.add(feedEntry.isOngoing() ? feedEntry.pause(finishTime): feedEntry);
        }
        return new Feed(id, babyId, finishedFeedEntryList, finishTime);
    }

    public Long getId() { return id; }
    public Long getBabyId() { return babyId; }
    public List<FeedEntry> getEntries() { return entries; }
}
