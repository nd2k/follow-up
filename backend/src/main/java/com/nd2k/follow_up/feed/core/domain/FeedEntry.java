package com.nd2k.follow_up.feed.core.domain;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

public class FeedEntry {

    private final Long id;
    private final BreastSide breastSide;
    private final Instant startTime;
    private final long accumulatedSeconds;
    private final Instant activeSince;
    private final Instant lastStoppedAt;

    public FeedEntry(Long id, BreastSide breastSide, Instant startTime, long accumulatedSeconds, Instant activeSince, Instant lastStoppedAt) {
        this.id = id;
        this.breastSide = breastSide;
        this.startTime = startTime;
        this.accumulatedSeconds = accumulatedSeconds;
        this.activeSince = activeSince;
        this.lastStoppedAt = lastStoppedAt;
    }

    public static FeedEntry start(BreastSide breastSide, Instant startTime) {
        Objects.requireNonNull(breastSide);
        Objects.requireNonNull(startTime);
        return new FeedEntry(null, breastSide, startTime, 0, startTime, null);
    }

    public static FeedEntry startAndStop(BreastSide breastSide, Instant startTime, Instant endTime) {
        if (!endTime.isAfter(startTime)) throw new IllegalArgumentException("le timestamp de fin ne peut pas être avant le timestamp de début");
        long durationInSeconds = Duration.between(startTime, endTime).getSeconds();
        return new FeedEntry(null, breastSide, startTime, durationInSeconds, null, endTime);
    }

    public static FeedEntry reconstitute(Long id, BreastSide breastSide, Instant startTime, long accumulatedSeconds, Instant activeSince, Instant lastStoppedAt) {
        return new FeedEntry(id, breastSide, startTime, accumulatedSeconds, activeSince, lastStoppedAt);
    }

    public FeedEntry pause(Instant pauseTime) {
        if (activeSince == null) throw new IllegalStateException("Cette tétée n'est pas en cours");
        if (pauseTime.isBefore(activeSince)) throw new IllegalArgumentException("pauseTime ne peut pas précéder le dernier démarrage");
        long delta = Duration.between(activeSince, pauseTime).getSeconds();
        return new FeedEntry(id, breastSide, startTime, accumulatedSeconds + delta, null, pauseTime);
    }

    public FeedEntry resume(Instant resumeTime) {
        if (activeSince != null) throw new IllegalStateException("Cette entrée est déjà en cours");
        return new FeedEntry(id, breastSide, startTime, accumulatedSeconds, resumeTime, lastStoppedAt);
    }

    public boolean isOngoing() { return activeSince != null; }

    public long durationInSeconds() {
        long extra = isOngoing() ? Duration.between(activeSince, Instant.now()).getSeconds() : 0;
        return accumulatedSeconds + extra;
    }

    public FeedEntry editTime(Instant newStartTime, Instant newEndTime) {
        if (isOngoing()) {
            throw new IllegalStateException("Impossible de modifier une entrée en cours — mets-la en pause d'abord");
        }
        if (!newEndTime.isAfter(newStartTime)) {
            throw new IllegalArgumentException("L'heure de fin doit être après l'heure de début");
        }
        long newAccumulatedSeconds = Duration.between(newStartTime, newEndTime).getSeconds();
        return new FeedEntry(id, breastSide, newStartTime, newAccumulatedSeconds, null, newEndTime);
    }

    public Long getId() { return id; }
    public BreastSide getBreastSide() { return breastSide; }
    public Instant getStartTime() { return startTime; }
    public Instant getActiveSince() { return activeSince; }
    public Instant getLastStoppedAt() { return lastStoppedAt; }
}
