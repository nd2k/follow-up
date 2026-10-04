package com.nd2k.follow_up.feed.core.port.in;

import com.nd2k.follow_up.feed.core.domain.Feed;

import java.time.Instant;

public interface FinishedFeedEntryUseCase {
    Feed finish(Long babyId, Long requestingUserId, Long feedId, Instant clientFinishedTime);
}
