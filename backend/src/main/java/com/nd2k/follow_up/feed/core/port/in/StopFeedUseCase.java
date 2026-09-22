package com.nd2k.follow_up.feed.core.port.in;

import com.nd2k.follow_up.feed.core.domain.Feed;

import java.time.Instant;

public interface StopFeedUseCase {

    Feed stopFeed(Long babyId, Long requestingUserId, Long id, Instant clientEndTime);
}
