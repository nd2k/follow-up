package com.nd2k.follow_up.feed.core.port.in;

import com.nd2k.follow_up.feed.core.domain.FeedStats;

import java.time.LocalDate;

public interface GetStatsUseCase {

    FeedStats getStats(Long babyId, Long requestingUserId, LocalDate localDate);
}
