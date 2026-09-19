package com.nd2k.follow_up.user.out.persistence;

import com.nd2k.follow_up.user.core.domain.User;

final class UserMapper {
    private UserMapper() {}

    static UserEntity toEntity(User user) {
        return new UserEntity(user.getId(), user.getEmail(), user.getPasswordHash(), user.getName());
    }

    static User toDomain(UserEntity entity) {
        return User.reconstitute(entity.getId(), entity.getEmail(), entity.getPasswordHash(), entity.getName());
    }
}
