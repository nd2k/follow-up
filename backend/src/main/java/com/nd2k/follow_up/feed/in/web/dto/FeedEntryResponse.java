package com.nd2k.follow_up.feed.in.web.dto;

import com.nd2k.follow_up.feed.core.domain.FeedEntry;

import java.time.Instant;

public record FeedEntryResponse(Long id, String breastSide, Instant startTime, Instant endTime, boolean ongoing, Long durationSeconds, Long durationMinutes) {
    public static FeedEntryResponse from(FeedEntry entry) {
        long duration = entry.durationInSeconds() / 60;
        return new FeedEntryResponse(
                entry.getId(),
                entry.getBreastSide().name(),
                entry.getStartTime(),
                entry.getLastStoppedAt(),
                entry.isOngoing(),
                duration,
                duration / 60);
    }
}
