package com.nd2k.follow_up.baby.in.web.dto;

import com.nd2k.follow_up.baby.core.domain.Baby;

import java.time.LocalDate;

public record BabyResponse(Long id, String name, LocalDate birthDate) {
    public static BabyResponse from(Baby baby) {
        return new BabyResponse(baby.getId(), baby.getName(), baby.getBirthDate());
    }
}
