package com.nd2k.follow_up.feed.core.domain;

public class UnauthorizedFeedAccessException extends RuntimeException {
    public UnauthorizedFeedAccessException(Long babyId) {
        super("Accès refusé au bébé " + babyId);
    }
}
