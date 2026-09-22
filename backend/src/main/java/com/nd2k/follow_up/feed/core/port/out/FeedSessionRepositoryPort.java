package com.nd2k.follow_up.feed.core.port.out;

import com.nd2k.follow_up.feed.core.domain.FeedSession;

public interface FeedSessionRepositoryPort {
    FeedSession save(FeedSession session);
}