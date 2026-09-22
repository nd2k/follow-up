package com.nd2k.follow_up.feed.out.persistence;

import com.nd2k.follow_up.feed.core.domain.BreastSide;
import com.nd2k.follow_up.feed.core.domain.Feed;

final class FeedMapper {

    private FeedMapper() {}

    static FeedEntity toEntity(Feed feed) {
        BreastSideEntity breastSideEntity = BreastSideEntity.valueOf(feed.getBreastSide().name());
        return new FeedEntity(feed.getId(), feed.getBabyId(), feed.getSessionId(), breastSideEntity, feed.getStartTime(), feed.getEndTime());
    }

    static Feed toDomain(FeedEntity feedEntity) {
        BreastSide breastSide = BreastSide.valueOf(feedEntity.getBreastSide().name());
        return new Feed(feedEntity.getId(), feedEntity.getBabyId(), feedEntity.getSessionId(), breastSide, feedEntity.getStartTime(), feedEntity.getEndTime());
    }
}
