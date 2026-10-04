package com.nd2k.follow_up.baby.out.utils;

import com.nd2k.follow_up.baby.core.domain.Baby;
import com.nd2k.follow_up.baby.out.persistence.BabyEntity;

public final class BabyMapper {
    private BabyMapper() {}

    public static BabyEntity toEntity(Baby baby) {
        return new BabyEntity(baby.getId(), baby.getName(), baby.getBirthDate());
    }

    public static Baby toDomain(BabyEntity entity) {
        return Baby.reconstitute(entity.getId(), entity.getName(), entity.getBirthDate());
    }
}
