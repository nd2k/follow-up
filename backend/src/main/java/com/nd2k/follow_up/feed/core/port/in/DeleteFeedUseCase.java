package com.nd2k.follow_up.feed.core.port.in;

public interface DeleteFeedUseCase {

    void deleteFeed(Long babyId, Long requestingUserId, Long id);
}
