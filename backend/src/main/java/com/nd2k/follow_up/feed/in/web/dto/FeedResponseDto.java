package com.nd2k.follow_up.feed.in.web.dto;

import com.nd2k.follow_up.feed.core.domain.Feed;

import java.time.Instant;

public record FeedResponseDto(
        Long id,
        Long babyId,
        Long sessionId,
        String breastSide,
        Instant startTime,
        Instant endTime,
        boolean ongoing,
        Long durationMinutes
) {
    public static FeedResponseDto from(Feed feed) {
        Long duration = feed.isOngoing() ? null : feed.durationSeconds() / 60;
        return new FeedResponseDto(
                feed.getId(),
                feed.getBabyId(),
                feed.getSessionId(),
                feed.getBreastSide().name(),
                feed.getStartTime(),
                feed.getEndTime(),
                feed.isOngoing(),
                duration
        );
    }
}
