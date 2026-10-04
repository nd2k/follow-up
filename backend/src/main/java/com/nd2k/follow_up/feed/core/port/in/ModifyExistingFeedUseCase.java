package com.nd2k.follow_up.feed.core.port.in;

import com.nd2k.follow_up.feed.core.domain.Feed;
import com.nd2k.follow_up.feed.core.domain.FeedEntry;

import java.util.List;

public interface ModifyExistingFeedUseCase {

    Feed modifyExistingFeed(List<FeedEntry> entries);
}
