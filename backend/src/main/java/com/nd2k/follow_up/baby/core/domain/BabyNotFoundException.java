package com.nd2k.follow_up.baby.core.domain;

public class BabyNotFoundException extends RuntimeException {
    public BabyNotFoundException(Long id) {
        super("Aucun bébé trouvé avec l'id " + id);
    }
}
