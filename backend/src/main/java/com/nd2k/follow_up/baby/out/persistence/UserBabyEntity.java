package com.nd2k.follow_up.baby.out.persistence;

import jakarta.persistence.*;

@Entity
@Table(name = "user_baby", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "baby_id"}))
public class UserBabyEntity {

    @SuppressWarnings("unused")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "baby_id", nullable = false)
    private Long babyId;

    @SuppressWarnings("unused")
    protected UserBabyEntity() {}

    public UserBabyEntity(Long userId, Long babyId) {
        this.userId = userId;
        this.babyId = babyId;
    }

    public Long getUserId() { return userId; }
    public Long getBabyId() { return babyId; }
}
