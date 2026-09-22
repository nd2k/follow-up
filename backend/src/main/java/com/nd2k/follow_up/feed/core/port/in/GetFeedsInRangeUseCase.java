package com.nd2k.follow_up.feed.core.port.in;

import com.nd2k.follow_up.feed.core.domain.Feed;

import java.time.Instant;
import java.util.List;

public interface GetFeedsInRangeUseCase {
    List<Feed> getForBabyInRange(Long babyId, Long requestingUserId, Instant from, Instant to);
}
