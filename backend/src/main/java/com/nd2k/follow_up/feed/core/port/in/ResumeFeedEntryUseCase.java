package com.nd2k.follow_up.feed.core.port.in;

import com.nd2k.follow_up.feed.core.domain.Feed;

import java.time.Instant;

public interface ResumeFeedEntryUseCase {
    Feed resumeEntry(Long babyId, Long requestingUserId, Long feedId, Long entryId, Instant clientResumeTime);
}
