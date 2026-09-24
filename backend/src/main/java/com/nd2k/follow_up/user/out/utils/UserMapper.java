package com.nd2k.follow_up.user.out.utils;

import com.nd2k.follow_up.user.core.domain.User;
import com.nd2k.follow_up.user.out.persistence.UserEntity;

public final class UserMapper {
    private UserMapper() {}

    public static UserEntity toEntity(User user) {
        return new UserEntity(user.getId(), user.getEmail(), user.getPasswordHash(), user.getName());
    }

    public static User toDomain(UserEntity entity) {
        return User.reconstitute(entity.getId(), entity.getEmail(), entity.getPasswordHash(), entity.getName());
    }
}
