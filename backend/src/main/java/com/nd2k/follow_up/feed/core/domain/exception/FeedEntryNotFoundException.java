package com.nd2k.follow_up.feed.core.domain.exception;

public class FeedEntryNotFoundException extends RuntimeException {
    public FeedEntryNotFoundException(Long id) { super("Aucune entrée trouvée avec l'id " + id); }
}
