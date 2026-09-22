package com.nd2k.follow_up.feed.in.web;

import com.nd2k.follow_up.feed.core.domain.Feed;
import com.nd2k.follow_up.feed.core.port.in.*;
import com.nd2k.follow_up.feed.in.web.dto.FeedRequestDto;
import com.nd2k.follow_up.feed.in.web.dto.FeedResponseDto;
import com.nd2k.follow_up.feed.in.web.dto.StatsResponseDto;
import com.nd2k.follow_up.feed.in.web.dto.StopFeedRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
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
    private final GetFeedsInRangeUseCase getFeedsInRangeUseCase;

    public FeedController(StartFeedUseCase startFeedUseCase,
                          StopFeedUseCase stopFeedUseCase,
                          ListFeedsUseCase listFeedsUseCase,
                          DeleteFeedUseCase deleteFeedUseCase,
                          GetStatsUseCase getStatsUseCase,
                          GetFeedsInRangeUseCase getFeedsInRangeUseCase) {
        this.startFeedUseCase = startFeedUseCase;
        this.stopFeedUseCase = stopFeedUseCase;
        this.listFeedsUseCase = listFeedsUseCase;
        this.deleteFeedUseCase = deleteFeedUseCase;
        this.getStatsUseCase = getStatsUseCase;
        this.getFeedsInRangeUseCase = getFeedsInRangeUseCase;
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
                                                @RequestBody(required = false) StopFeedRequest request,
                                                Authentication authentication) {
        Instant clientEndTime = request != null ? request.endTime() : null;
        return FeedResponseDto.from(stopFeedUseCase.stopFeed(babyId, userId(authentication), feedId, clientEndTime));
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

    @GetMapping("/feeds/range")
    public List<FeedResponseDto> getInRange(
            @PathVariable Long babyId,
            @RequestParam Instant from,
            @RequestParam Instant to,
            Authentication auth) {
        return getFeedsInRangeUseCase.getForBabyInRange(babyId, userId(auth), from, to).stream()
                .map(FeedResponseDto::from)
                .toList();
    }
}
