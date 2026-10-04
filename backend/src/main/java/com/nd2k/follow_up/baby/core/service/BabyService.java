package com.nd2k.follow_up.baby.core.service;

import com.nd2k.follow_up.baby.core.domain.Baby;
import com.nd2k.follow_up.baby.core.port.in.CheckBabyAccessUseCase;
import com.nd2k.follow_up.baby.core.port.in.CreateBabyUseCase;
import com.nd2k.follow_up.baby.core.port.in.GetBabyUseCase;
import com.nd2k.follow_up.baby.core.port.in.ListBabiesUseCase;
import com.nd2k.follow_up.baby.core.port.out.BabyAccessRepositoryPort;
import com.nd2k.follow_up.baby.core.port.out.BabyRepositoryPort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class BabyService implements CreateBabyUseCase,
        GetBabyUseCase,
        ListBabiesUseCase,
        CheckBabyAccessUseCase {

    private final BabyRepositoryPort babyRepositoryPort;
    private final BabyAccessRepositoryPort babyAccessRepositoryPort;

    public BabyService(BabyRepositoryPort babyRepositoryPort,
                       BabyAccessRepositoryPort babyAccessRepositoryPort) {
        this.babyRepositoryPort = babyRepositoryPort;
        this.babyAccessRepositoryPort = babyAccessRepositoryPort;
    }

    @Override
    public Baby create(String name, LocalDate birthDate, Long creatorUserId, int size) {
        return null;
    }

    @Override
    public Baby getForUser(Long babyId, Long requestingUserId, int size) {
        return null;
    }

    @Override
    public List<Baby> listForUser(Long userId) {
        List<Long> babyIds = babyAccessRepositoryPort.findBabyIdsForUser(userId);
        return babyRepositoryPort.findAllByIds(babyIds);
    }

    @Override
    public boolean hasAccess(Long userId, Long babyId) {
        return babyAccessRepositoryPort.hasAccess(userId, babyId);
    }
}
