package com.nd2k.follow_up.baby.core.domain;

public class UnauthorizedBabyAccessException extends RuntimeException {
    public UnauthorizedBabyAccessException(Long babyId) {
        super("Accès refusé au bébé " + babyId);
    }
}
