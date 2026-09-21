package com.nd2k.follow_up.baby.core.port.out;

import java.util.List;

public interface BabyAccessRepositoryPort {
    void link(Long userId, Long babyId);
    boolean hasAccess(Long userId, Long babyId);
    List<Long> findBabyIdsForUser(Long userId);
}
