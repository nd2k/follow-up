package com.nd2k.follow_up.feed.core.port.in;

import com.nd2k.follow_up.feed.core.domain.Feed;

public interface StopFeedUseCase {

    Feed stopFeed(Long id);
}
