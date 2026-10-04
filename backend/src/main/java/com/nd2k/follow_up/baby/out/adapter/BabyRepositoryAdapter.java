package com.nd2k.follow_up.baby.out.adapter;

import com.nd2k.follow_up.baby.core.domain.Baby;
import com.nd2k.follow_up.baby.core.port.out.BabyRepositoryPort;
import com.nd2k.follow_up.baby.out.persistence.BabyJpaRepository;
import com.nd2k.follow_up.baby.out.utils.BabyMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class BabyRepositoryAdapter implements BabyRepositoryPort {

    private final BabyJpaRepository babyJpaRepository;

    public BabyRepositoryAdapter(BabyJpaRepository babyJpaRepository) {
        this.babyJpaRepository = babyJpaRepository;
    }

    @Override
    public Baby save(Baby baby) {
        return BabyMapper.toDomain(babyJpaRepository.save(BabyMapper.toEntity(baby)));
    }

    @Override
    public Optional<Baby> findById(Long id) {
        return babyJpaRepository.findById(id).map(BabyMapper::toDomain);
    }

    @Override
    public List<Baby> findAllByIds(List<Long> ids) {
        return babyJpaRepository.findAllByIdIn(ids).stream().map(BabyMapper::toDomain).toList();
    }
}
