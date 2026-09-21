package com.nd2k.follow_up.feed.core.port.out;

public interface BabyAccessCheckPort {
    boolean hasAccess(Long userId, Long babyId);
}
