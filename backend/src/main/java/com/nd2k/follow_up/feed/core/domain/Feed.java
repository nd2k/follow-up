package com.nd2k.follow_up.feed.core.domain;

import java.time.Instant;
import java.util.Objects;

public class Feed {

    private final Long id;
    private final BreastSide breastSide;
    private final Instant startTime;
    private final Instant endTime;

    public Feed(Long id, BreastSide breastSide, Instant startTime, Instant endTime) {
        this.id = id;
        this.breastSide = breastSide;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public static Feed startFeed(BreastSide breastSide, Instant startTime) {
        Objects.requireNonNull(breastSide, "Cannot create a Feed without a breast side");
        Objects.requireNonNull(startTime, "startTime ne cannot be null");
        return new Feed(null, breastSide, startTime, null);
    }

    public Feed stopFeed(Instant endTime) {
        if (this.endTime != null) {
            throw new IllegalStateException("This feed is already finished");
        }
        if (endTime.isBefore(this.startTime)) {
            throw new IllegalArgumentException("End time cannot be before start time");
        }
        return new Feed(this.id, this.breastSide, this.startTime, endTime);
    }

    public boolean isOngoing() {
        return endTime == null;
    }

    public long durationSeconds() {
        if (isOngoing()) {
            throw new IllegalStateException("Cannot calculate duration with ongoing feed");
        }
        return endTime.getEpochSecond() - startTime.getEpochSecond();
    }

    public Long getId() { return id; }
    public BreastSide getSide() { return breastSide; }
    public Instant getStartTime() { return startTime; }
    public Instant getEndTime() { return endTime; }
}
