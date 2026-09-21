package com.nd2k.follow_up.baby.in.web;

import com.nd2k.follow_up.baby.core.domain.Baby;
import com.nd2k.follow_up.baby.core.port.in.AddParentUseCase;
import com.nd2k.follow_up.baby.core.port.in.CreateBabyUseCase;
import com.nd2k.follow_up.baby.core.port.in.GetBabyUseCase;
import com.nd2k.follow_up.baby.core.port.in.ListBabiesUseCase;
import com.nd2k.follow_up.baby.in.web.dto.AddParentRequest;
import com.nd2k.follow_up.baby.in.web.dto.BabyResponse;
import com.nd2k.follow_up.baby.in.web.dto.CreateBabyRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/babies")
public class BabyController {

    private final CreateBabyUseCase createBabyUseCase;
    private final ListBabiesUseCase listBabiesUseCase;
    private final GetBabyUseCase getBabyUseCase;
    private final AddParentUseCase addParentUseCase;

    public BabyController(CreateBabyUseCase createBabyUseCase, ListBabiesUseCase listBabiesUseCase,
                          GetBabyUseCase getBabyUseCase, AddParentUseCase addParentUseCase) {
        this.createBabyUseCase = createBabyUseCase;
        this.listBabiesUseCase = listBabiesUseCase;
        this.getBabyUseCase = getBabyUseCase;
        this.addParentUseCase = addParentUseCase;
    }

    @PostMapping
    public ResponseEntity<BabyResponse> create(@Valid @RequestBody CreateBabyRequest request, Authentication auth) {
        Baby baby = createBabyUseCase.create(request.name(), request.birthDate(), currentUserId(auth));
        return ResponseEntity.status(HttpStatus.CREATED).body(BabyResponse.from(baby));
    }

    @GetMapping
    public List<BabyResponse> listMine(Authentication auth) {
        return listBabiesUseCase.listForUser(currentUserId(auth)).stream()
                .map(BabyResponse::from)
                .toList();
    }

    @GetMapping("/{id}")
    public BabyResponse getOne(@PathVariable Long id, Authentication auth) {
        return BabyResponse.from(getBabyUseCase.getForUser(id, currentUserId(auth)));
    }

    @PostMapping("/{id}/parents")
    public ResponseEntity<Void> addParent(@PathVariable Long id, @Valid @RequestBody AddParentRequest request, Authentication auth) {
        addParentUseCase.addParent(id, currentUserId(auth), request.email());
        return ResponseEntity.noContent().build();
    }

    private Long currentUserId(Authentication auth) {
        return Long.parseLong(auth.getName());
    }
}
