package com.nd2k.follow_up.feed.core.domain;

public class FeedSession {

    private final Long id;
    private final Long babyId;

    private FeedSession(Long id, Long babyId) {
        this.id = id;
        this.babyId = babyId;
    }

    public static FeedSession create(Long babyId) {
        return new FeedSession(null, babyId);
    }

    public static FeedSession reconstitute(Long id, Long babyId) {
        return new FeedSession(id, babyId);
    }

    public Long getId() { return id; }
    public Long getBabyId() { return babyId; }
}
