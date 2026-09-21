package com.nd2k.follow_up.baby.core.port.out;

import java.util.Optional;

public interface UserLookupPort {
    Optional<Long> findUserIdByEmail(String email);
}
