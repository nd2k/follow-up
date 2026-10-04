package com.nd2k.follow_up.feed.core.port.in;

import com.nd2k.follow_up.feed.core.domain.Feed;

import java.time.Instant;

public interface PauseFeedEntryUseCase {
    Feed pauseEntry(Long babyId, Long requestingUserId, Long feedId, Long entryId, Instant clientPauseTime);
}
