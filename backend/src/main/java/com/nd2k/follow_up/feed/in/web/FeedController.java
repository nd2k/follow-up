package com.nd2k.follow_up.feed.in.web;

import com.nd2k.follow_up.feed.core.domain.Feed;
import com.nd2k.follow_up.feed.core.port.in.*;
import com.nd2k.follow_up.feed.in.web.dto.FeedRequestDto;
import com.nd2k.follow_up.feed.in.web.dto.FeedResponseDto;
import com.nd2k.follow_up.feed.in.web.dto.StatsResponseDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1")
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

    @PostMapping("/feeds/start")
    private ResponseEntity<FeedResponseDto> addFeed(@Valid @RequestBody FeedRequestDto feedRequestDto) {
        Feed feed = startFeedUseCase.startFeed(feedRequestDto.breastSide());
        return ResponseEntity.status(HttpStatus.CREATED).body(FeedResponseDto.from(feed));
    }

    @PostMapping("/feeds/{id}/stop")
    public ResponseEntity<FeedResponseDto> stop(@PathVariable Long id) {
        Feed feed = stopFeedUseCase.stopFeed(id);
        return ResponseEntity.ok(FeedResponseDto.from(feed));
    }

    @GetMapping("/feeds")
    public ResponseEntity<List<FeedResponseDto>> listAll() {
            List<FeedResponseDto> feeds = listFeedsUseCase.listAllFeed().stream()
                .map(FeedResponseDto::from)
                .toList();
        return ResponseEntity.ok(feeds);
    }

    @DeleteMapping("/feeds/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        deleteFeedUseCase.deleteFeed(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stats/today")
    public ResponseEntity<StatsResponseDto> statsToday() {
        StatsResponseDto stats = StatsResponseDto.from(getStatsUseCase.getStats(LocalDate.now()));
        return ResponseEntity.ok(stats);
    }
}
