package com.nd2k.follow_up.baby.core.port.in;

public interface AddParentUseCase {
    void addParent(Long babyId, Long requestingUserId, String newParentEmail);
}
