package com.nd2k.follow_up.baby.core.port.in;

import com.nd2k.follow_up.baby.core.domain.Baby;

import java.util.List;

public interface ListBabiesUseCase {
    List<Baby> listForUser(Long userId);
}
