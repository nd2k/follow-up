package com.nd2k.follow_up.baby.core.port.out;

import com.nd2k.follow_up.baby.core.domain.Baby;

import java.util.List;
import java.util.Optional;

public interface BabyRepositoryPort {
    Baby save(Baby baby);
    Optional<Baby> findById(Long id);
    List<Baby> findAllByIds(List<Long> ids);
}
