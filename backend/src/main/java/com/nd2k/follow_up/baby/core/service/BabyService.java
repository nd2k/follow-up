package com.nd2k.follow_up.baby.core.service;

import com.nd2k.follow_up.baby.core.domain.Baby;
import com.nd2k.follow_up.baby.core.domain.BabyNotFoundException;
import com.nd2k.follow_up.baby.core.domain.ParentNotFoundException;
import com.nd2k.follow_up.baby.core.domain.UnauthorizedBabyAccessException;
import com.nd2k.follow_up.baby.core.port.in.*;
import com.nd2k.follow_up.baby.core.port.out.BabyAccessRepositoryPort;
import com.nd2k.follow_up.baby.core.port.out.BabyRepositoryPort;
import com.nd2k.follow_up.baby.core.port.out.UserLookupPort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class BabyService implements
        CreateBabyUseCase,
        ListBabiesUseCase,
        GetBabyUseCase,
        CheckBabyAccessUseCase,
        AddParentUseCase {

    private final BabyRepositoryPort babyRepository;
    private final BabyAccessRepositoryPort babyAccessRepository;
    private final UserLookupPort userLookup;

    public BabyService(BabyRepositoryPort babyRepository, BabyAccessRepositoryPort babyAccessRepository, UserLookupPort userLookup) {
        this.babyRepository = babyRepository;
        this.babyAccessRepository = babyAccessRepository;
        this.userLookup = userLookup;
    }

    @Override
    public Baby create(String name, LocalDate birthDate, Long creatorUserId) {
        Baby saved = babyRepository.save(Baby.create(name, birthDate));
        babyAccessRepository.link(creatorUserId, saved.getId());
        return saved;
    }

    @Override
    public List<Baby> listForUser(Long userId) {
        List<Long> babyIds = babyAccessRepository.findBabyIdsForUser(userId);
        return babyRepository.findAllByIds(babyIds);
    }

    @Override
    public Baby getForUser(Long babyId, Long requestingUserId) {
        if (!babyAccessRepository.hasAccess(requestingUserId, babyId)) {
            throw new UnauthorizedBabyAccessException(babyId);
        }
        return babyRepository.findById(babyId)
                .orElseThrow(() -> new BabyNotFoundException(babyId));
    }

    @Override
    public boolean hasAccess(Long userId, Long babyId) {
        return babyAccessRepository.hasAccess(userId, babyId);
    }

    @Override
    public void addParent(Long babyId, Long requestingUserId, String newParentEmail) {
        if (!babyAccessRepository.hasAccess(requestingUserId, babyId)) {
            throw new UnauthorizedBabyAccessException(babyId);
        }
        Long newParentId = userLookup.findUserIdByEmail(newParentEmail)
                .orElseThrow(() -> new ParentNotFoundException(newParentEmail));
        babyAccessRepository.link(newParentId, babyId);
    }
}
