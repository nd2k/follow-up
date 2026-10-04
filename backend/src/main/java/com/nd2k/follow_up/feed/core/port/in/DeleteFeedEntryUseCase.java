package com.nd2k.follow_up.feed.core.port.in;

public interface DeleteFeedEntryUseCase {
    void deleteEntry(Long babyId, Long requestingUserId, Long feedId, Long entryId);
}
