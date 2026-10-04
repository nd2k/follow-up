package com.nd2k.follow_up.baby.in.web;

import com.nd2k.follow_up.baby.core.port.in.ListBabiesUseCase;
import com.nd2k.follow_up.baby.in.web.dto.BabyResponse;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/babies")
public class BabyController {

    private final ListBabiesUseCase listBabiesUseCase;

    public BabyController(ListBabiesUseCase listBabiesUseCase) {
        this.listBabiesUseCase = listBabiesUseCase;
    }

    private Long currentUserId(Authentication authentication) {
        return Long.parseLong(authentication.getName());
    }

    @GetMapping
    public List<BabyResponse> listBabies(Authentication authentication) {
        Long userId = currentUserId(authentication);
        return listBabiesUseCase.listForUser(userId).stream()
                .map(BabyResponse::from)
                .toList();
    }
}
