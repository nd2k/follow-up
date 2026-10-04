package com.nd2k.follow_up.feed.out.persistence;

import com.nd2k.follow_up.feed.core.domain.BreastSide;
import com.nd2k.follow_up.feed.core.domain.Feed;
import com.nd2k.follow_up.feed.core.domain.FeedEntry;

import java.util.List;

public final class FeedMapper {

    private FeedMapper() {}

    public static FeedEntryEntity toEntity(FeedEntry feedEntry, Long feedId, Long babyId) {
        BreastSideEntity breastSideEntity = BreastSideEntity.valueOf(feedEntry.getBreastSide().name());
        return new FeedEntryEntity(
                feedEntry.getId(),
                feedId,
                babyId,
                breastSideEntity,
                feedEntry.getStartTime(),
                feedEntry.durationInSeconds(),
                feedEntry.getActiveSince(),
                feedEntry.getLastStoppedAt());
    }

    public static Feed toDomain(AggregateFeedEntity aggregateFeedEntity, List<FeedEntryEntity> listOfEntriesEntity) {

        return new Feed(
                aggregateFeedEntity.getId(),
                aggregateFeedEntity.getBabyId(),
                listOfEntriesEntity.stream()
                        .map(e -> FeedEntry.reconstitute(
                                    e.getId(),
                                    BreastSide.valueOf(e.getBreastSideEntity().name()),
                                    e.getStartTime(),
                                    e.getAccumulatedInSeconds(),
                                    e.getActiveSince(),
                                    e.getLastStoppedAt())
                        )
                        .toList(),
                aggregateFeedEntity.getFinishedAt());
    }
}
