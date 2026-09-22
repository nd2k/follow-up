package com.nd2k.follow_up.feed.out.persistence;

import com.nd2k.follow_up.feed.core.domain.FeedSession;
import com.nd2k.follow_up.feed.core.port.out.FeedSessionRepositoryPort;
import org.springframework.stereotype.Component;

@Component
class FeedSessionPersistenceAdapter implements FeedSessionRepositoryPort {

    private final FeedSessionJpaRepository jpaRepository;

    FeedSessionPersistenceAdapter(FeedSessionJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public FeedSession save(FeedSession session) {
        FeedSessionEntity saved = jpaRepository.save(new FeedSessionEntity(session.getId(), session.getBabyId()));
        return FeedSession.reconstitute(saved.getId(), saved.getBabyId());
    }
}
