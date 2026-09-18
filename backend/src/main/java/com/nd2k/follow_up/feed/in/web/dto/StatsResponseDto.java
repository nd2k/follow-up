package com.nd2k.follow_up.feed.in.web.dto;

import com.nd2k.follow_up.feed.core.domain.FeedStats;

public record StatsResponseDto(int count, long totalDurationInMinutes, long averageInMinutes) {

    public static StatsResponseDto from(FeedStats feedStats) {
        return new StatsResponseDto(feedStats.count(), feedStats.totalDurationInMinutes(), feedStats.averageInMinutes());
    }
}
