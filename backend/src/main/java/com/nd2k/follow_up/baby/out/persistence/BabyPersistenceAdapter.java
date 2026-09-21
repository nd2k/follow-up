package com.nd2k.follow_up.baby.out.persistence;

import com.nd2k.follow_up.baby.core.domain.Baby;
import com.nd2k.follow_up.baby.core.port.out.BabyRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
class BabyPersistenceAdapter implements BabyRepositoryPort {

    private final BabyJpaRepository jpaRepository;

    BabyPersistenceAdapter(BabyJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Baby save(Baby baby) {
        return BabyMapper.toDomain(jpaRepository.save(BabyMapper.toEntity(baby)));
    }

    @Override
    public Optional<Baby> findById(Long id) {
        return jpaRepository.findById(id).map(BabyMapper::toDomain);
    }

    @Override
    public List<Baby> findAllByIds(List<Long> ids) {
        return jpaRepository.findAllByIdIn(ids).stream().map(BabyMapper::toDomain).toList();
    }
}
