package com.nd2k.follow_up.baby.core.domain;

public class ParentNotFoundException extends RuntimeException {
    public ParentNotFoundException(String email) {
        super("Aucun utilisateur trouvé avec l'email " + email);
    }
}
