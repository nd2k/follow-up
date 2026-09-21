package com.nd2k.follow_up.feed.in.web;

import com.nd2k.follow_up.feed.core.domain.Feed;
import com.nd2k.follow_up.feed.core.port.in.*;
import com.nd2k.follow_up.feed.in.web.dto.FeedRequestDto;
import com.nd2k.follow_up.feed.in.web.dto.FeedResponseDto;
import com.nd2k.follow_up.feed.in.web.dto.StatsResponseDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/babies/{babyId}")
public class FeedController {

    private final StartFeedUseCase startFeedUseCase;
    private final StopFeedUseCase stopFeedUseCase;
    private final ListFeedsUseCase listFeedsUseCase;
    private final DeleteFeedUseCase deleteFeedUseCase;
    private final GetStatsUseCase getStatsUseCase;

    public FeedController(StartFeedUseCase startFeedUseCase,
                          StopFeedUseCase stopFeedUseCase,
                          ListFeedsUseCase listFeedsUseCase,
                          DeleteFeedUseCase deleteFeedUseCase,
                          GetStatsUseCase getStatsUseCase) {
        this.startFeedUseCase = startFeedUseCase;
        this.stopFeedUseCase = stopFeedUseCase;
        this.listFeedsUseCase = listFeedsUseCase;
        this.deleteFeedUseCase = deleteFeedUseCase;
        this.getStatsUseCase = getStatsUseCase;
    }

    private Long userId(Authentication auth) {
        return Long.parseLong(auth.getName());
    }

    @PostMapping("/feeds/start")
    private ResponseEntity<FeedResponseDto> addFeed(@PathVariable Long babyId,
                                                    @Valid @RequestBody FeedRequestDto feedRequestDto,
                                                    Authentication authentication) {
        Feed feed = startFeedUseCase.startFeed(babyId, userId(authentication), feedRequestDto.breastSide(), feedRequestDto.clientStartTime());
        return ResponseEntity.status(HttpStatus.CREATED).body(FeedResponseDto.from(feed));
    }

    @PostMapping("/feeds/{feedId}/stop")
    public FeedResponseDto stop(@PathVariable Long babyId,
                                                @PathVariable Long feedId,
                                                Authentication authentication) {
        return FeedResponseDto.from(stopFeedUseCase.stopFeed(babyId, userId(authentication), feedId));
    }

    @GetMapping("/feeds")
    public List<FeedResponseDto> list(@PathVariable Long babyId, Authentication auth) {
        return listFeedsUseCase.listAllFeed(babyId, userId(auth)).stream().map(FeedResponseDto::from).toList();
    }

    @DeleteMapping("/feeds/{feedId}")
    public ResponseEntity<Void> delete(@PathVariable Long babyId, @PathVariable Long feedId, Authentication auth) {
        deleteFeedUseCase.deleteFeed(babyId, userId(auth), feedId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stats/today")
    public StatsResponseDto statsToday(@PathVariable Long babyId, Authentication auth) {
        return StatsResponseDto.from(getStatsUseCase.getStats(babyId, userId(auth), LocalDate.now()));
    }
}
