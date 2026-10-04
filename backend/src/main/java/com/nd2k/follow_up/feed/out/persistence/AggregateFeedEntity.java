package com.nd2k.follow_up.feed.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "aggregate_feeds")
public class AggregateFeedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "baby_id", nullable = false)
    private Long babyId;
    @Column(name = "finished_at")
    private Instant finishedAt;

    protected AggregateFeedEntity() {}
    public AggregateFeedEntity(Long id, Long babyId, Instant finishedAt) {
        this.id = id;
        this.babyId = babyId;
        this.finishedAt = finishedAt;
    }

    public Long getId() { return id; }
    public Long getBabyId() { return babyId; }
    public Instant getFinishedAt() { return finishedAt; }
   }
