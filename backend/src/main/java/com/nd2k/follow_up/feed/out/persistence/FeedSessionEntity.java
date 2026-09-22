package com.nd2k.follow_up.feed.out.persistence;

import jakarta.persistence.*;

@Entity
@Table(name = "feed_sessions")
public class FeedSessionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "baby_id", nullable = false)
    private Long babyId;

    @SuppressWarnings("unused")
    protected FeedSessionEntity() {}

    public FeedSessionEntity(Long id, Long babyId) {
        this.id = id;
        this.babyId = babyId;
    }

    public Long getId() { return id; }
    public Long getBabyId() { return babyId; }
}
