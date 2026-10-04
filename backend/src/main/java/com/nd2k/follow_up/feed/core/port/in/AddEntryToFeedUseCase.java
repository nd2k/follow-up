package com.nd2k.follow_up.feed.core.port.in;

import com.nd2k.follow_up.feed.core.domain.BreastSide;
import com.nd2k.follow_up.feed.core.domain.Feed;

import java.time.Instant;

public interface AddEntryToFeedUseCase {
    Feed addEntry(Long babyId, Long requestingUserId, Long feedId, BreastSide breastSide, Instant startTime, Instant endTime);
}
