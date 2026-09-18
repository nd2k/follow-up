package com.nd2k.follow_up.feed.core.port.in;

import com.nd2k.follow_up.feed.core.domain.BreastSide;
import com.nd2k.follow_up.feed.core.domain.Feed;

public interface StartFeedUseCase {

    Feed startFeed(BreastSide breastSide);
}
