package com.nd2k.follow_up.feed.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "feed_entries")
public class FeedEntryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "feed_id", nullable = false)
    private Long feedId;
    @Column(name = "baby_id", nullable = false)
    private Long babyId;
    @Enumerated(EnumType.STRING)
    @Column(name = "breast_side", nullable = false)
    private BreastSideEntity breastSideEntity;
    @Column(name = "start_time",nullable = false)
    private Instant startTime;
    @Column(name = "accumulated_seconds", nullable = false)
    private long accumulatedSInSeconds;
    @Column(name = "active_since")
    private Instant activeSince;
    @Column(name = "last_stopped_at")
    private Instant lastStoppedAt;

    @SuppressWarnings("unused")
    protected FeedEntryEntity() {}
    public FeedEntryEntity(Long id, Long feedId, Long babyId, BreastSideEntity breastSideEntity, Instant startTime, long accumulatedInSeconds, Instant activeSince, Instant lastStoppedAt) {
        this.id = id;
        this.feedId = feedId;
        this.babyId = babyId;
        this.breastSideEntity = breastSideEntity;
        this.startTime = startTime;
        this.accumulatedSInSeconds = accumulatedInSeconds;
        this.activeSince = activeSince;
        this.lastStoppedAt = lastStoppedAt;
    }

    public Long getId() { return id; }
    public Long getFeedId() { return feedId; }
    public Long getBabyId() { return babyId; }
    public BreastSideEntity getBreastSideEntity() { return breastSideEntity; }
    public Instant getStartTime() { return startTime; }
    public Long getAccumulatedInSeconds() { return accumulatedSInSeconds; }
    public Instant getActiveSince() { return activeSince; }
    public Instant getLastStoppedAt() { return lastStoppedAt; }
}
