package com.nd2k.follow_up.feed.out.crossmodule;

import com.nd2k.follow_up.baby.core.port.in.CheckBabyAccessUseCase;
import com.nd2k.follow_up.feed.core.port.out.BabyAccessCheckPort;
import org.springframework.stereotype.Component;

@Component
class BabyAccessCheckAdapter implements BabyAccessCheckPort {

    private final CheckBabyAccessUseCase checkBabyAccessUseCase;

    BabyAccessCheckAdapter(CheckBabyAccessUseCase checkBabyAccessUseCase) {
        this.checkBabyAccessUseCase = checkBabyAccessUseCase;
    }

    @Override
    public boolean hasAccess(Long userId, Long babyId) {
        return checkBabyAccessUseCase.hasAccess(userId, babyId);
    }
}
