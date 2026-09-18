package com.nd2k.follow_up.feed.out.persistence;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "feeds")
public class FeedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BreastSideEntity breastSide;
    @Column(nullable = false)
    private Instant startTime;
    @Column
    private Instant endTime;

    protected FeedEntity() {
        throw new UnsupportedOperationException("This is a Entity class and cannot be instantiated");
    }

    public FeedEntity(Long id, BreastSideEntity breastSide, Instant startTime, Instant endTime) {
        this.id = id;
        this.breastSide = breastSide;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Long getId() { return id; }
    public BreastSideEntity getBreastSide() { return breastSide; }
    public Instant getStartTime() { return startTime; }
    public Instant getEndTime() { return endTime; }
}
