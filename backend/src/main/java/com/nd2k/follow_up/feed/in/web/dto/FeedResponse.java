package com.nd2k.follow_up.feed.in.web.dto;

import com.nd2k.follow_up.feed.core.domain.Feed;

import java.time.Instant;
import java.util.List;

public record FeedResponse(Long id, Long babyId, Instant startTime, Instant endTime, boolean ongoing, List<FeedEntryResponse> entries) {
    public static FeedResponse from(Feed feed) {
        return new FeedResponse(
                feed.getId(),
                feed.getBabyId(),
                feed.getStartTime(),
                feed.isOngoing() ? null : feed.getEndTime(),
                feed.isOngoing(),
                feed.getEntries().stream().map(FeedEntryResponse::from).toList()
        );
    }
}
