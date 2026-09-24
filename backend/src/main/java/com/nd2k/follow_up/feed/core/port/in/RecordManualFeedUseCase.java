package com.nd2k.follow_up.feed.core.port.in;

import com.nd2k.follow_up.feed.core.domain.BreastSide;
import com.nd2k.follow_up.feed.core.domain.Feed;

import java.time.Instant;
import java.util.List;

public interface RecordManualFeedUseCase {
    List<Feed> record(Long babyId, Long requestingUserId, List<SideEntry> entries);

    record SideEntry(BreastSide breastSide, Instant startTime, Instant endTime) {}
}