package com.nd2k.follow_up.feed.core.domain;

public class FeedNotFoundException extends RuntimeException {

    public FeedNotFoundException(Long id) {
        super("No feed found with Id --- " + id);
    }
}
