package com.nd2k.follow_up.feed.core.domain.exception;

public class FeedNotFoundException extends RuntimeException {
    public FeedNotFoundException(Long id) { super("Aucune tétée trouvée avec l'id " + id); }
}
