package com.nd2k.follow_up.feed.in.web.dto;

import com.nd2k.follow_up.feed.core.domain.FeedStats;

public record StatsResponse(int count, long totalDurationInMinutes, long averageInMinutes) {

    public static StatsResponse from(FeedStats feedStats) {
        return new StatsResponse(feedStats.count(), feedStats.totalDurationInMinutes(), feedStats.averageInMinutes());
    }
}
