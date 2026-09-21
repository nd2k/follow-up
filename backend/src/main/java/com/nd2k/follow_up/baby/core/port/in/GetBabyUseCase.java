package com.nd2k.follow_up.baby.core.port.in;

import com.nd2k.follow_up.baby.core.domain.Baby;

public interface GetBabyUseCase {
    Baby getForUser(Long babyId, Long requestingUserId);
}
