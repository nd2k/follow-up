package com.nd2k.follow_up.user.core.domain;

public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException() {
        super("Email ou mot de passe incorrect");
    }
}
