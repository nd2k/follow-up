package com.nd2k.follow_up.feed.out.persistence;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "feeds")
public class FeedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "baby_id", nullable = false)
    private Long babyId;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BreastSideEntity breastSide;
    @Column(nullable = false)
    private Instant startTime;
    @Column
    private Instant endTime;

    @SuppressWarnings("unused")
    protected FeedEntity() {
        // required by JPA
    }

    public FeedEntity(Long id, Long babyId, BreastSideEntity breastSide, Instant startTime, Instant endTime) {
        this.id = id;
        this.babyId = babyId;
        this.breastSide = breastSide;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Long getId() { return id; }
    public Long getBabyId() { return babyId; }
    public BreastSideEntity getBreastSide() { return breastSide; }
    public Instant getStartTime() { return startTime; }
    public Instant getEndTime() { return endTime; }
}
