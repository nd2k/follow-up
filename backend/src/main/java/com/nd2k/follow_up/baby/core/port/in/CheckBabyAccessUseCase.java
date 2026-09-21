package com.nd2k.follow_up.baby.core.port.in;

public interface CheckBabyAccessUseCase {
    boolean hasAccess(Long userId, Long babyId);
}
