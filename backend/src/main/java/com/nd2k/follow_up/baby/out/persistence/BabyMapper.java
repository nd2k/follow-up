package com.nd2k.follow_up.baby.out.persistence;

import com.nd2k.follow_up.baby.core.domain.Baby;

final class BabyMapper {
    private BabyMapper() {}

    static BabyEntity toEntity(Baby baby) {
        return new BabyEntity(baby.getId(), baby.getName(), baby.getBirthDate());
    }

    static Baby toDomain(BabyEntity entity) {
        return Baby.reconstitute(entity.getId(), entity.getName(), entity.getBirthDate());
    }
}
