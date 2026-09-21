package com.nd2k.follow_up.baby.core.port.in;

import com.nd2k.follow_up.baby.core.domain.Baby;

import java.time.LocalDate;

public interface CreateBabyUseCase {
    Baby create(String name, LocalDate birthDate, Long creatorUserId);
}
