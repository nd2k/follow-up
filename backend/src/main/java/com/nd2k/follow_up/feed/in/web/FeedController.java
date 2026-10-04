package com.nd2k.follow_up.feed.in.web;

import com.nd2k.follow_up.feed.core.domain.Feed;
import com.nd2k.follow_up.feed.core.port.in.*;
import com.nd2k.follow_up.feed.in.web.dto.*;
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

    private final StartFeedEntryUseCase startFeedEntryUseCase;
    private final ListFeedsUseCase listFeedsUseCase;
    private final DeleteFeedEntryUseCase deleteFeedEntryUseCase;
    private final GetStatsUseCase getStatsUseCase;
    private final GetFeedsInRangeUseCase getFeedsInRangeUseCase;
    private final RecordManualFeedUseCase recordManualFeedUseCase;
    private final PauseFeedEntryUseCase pauseFeedEntryUseCase;
    private final ResumeFeedEntryUseCase resumeFeedEntryUseCase;
    private final FinishedFeedEntryUseCase finishedFeedEntryUseCase;
    private final EditFeedEntryUseCase editFeedEntryUseCase;
    private final AddEntryToFeedUseCase addEntryToFeedUseCase;

    public FeedController(StartFeedEntryUseCase startFeedEntryUseCase,
                          ListFeedsUseCase listFeedsUseCase, DeleteFeedEntryUseCase deleteFeedEntryUseCase,
                          GetStatsUseCase getStatsUseCase, GetFeedsInRangeUseCase getFeedsInRangeUseCase,
                          RecordManualFeedUseCase recordManualFeedUseCase,
                          PauseFeedEntryUseCase pauseFeedEntryUseCase,
                          ResumeFeedEntryUseCase resumeFeedEntryUseCase,
                          FinishedFeedEntryUseCase finishedFeedEntryUseCase,
                          EditFeedEntryUseCase editFeedEntryUseCase,
                          AddEntryToFeedUseCase addEntryToFeedUseCase) {
        this.startFeedEntryUseCase = startFeedEntryUseCase;
        this.listFeedsUseCase = listFeedsUseCase;
        this.deleteFeedEntryUseCase = deleteFeedEntryUseCase;
        this.getStatsUseCase = getStatsUseCase;
        this.getFeedsInRangeUseCase = getFeedsInRangeUseCase;
        this.recordManualFeedUseCase = recordManualFeedUseCase;
        this.pauseFeedEntryUseCase = pauseFeedEntryUseCase;
        this.resumeFeedEntryUseCase = resumeFeedEntryUseCase;
        this.finishedFeedEntryUseCase = finishedFeedEntryUseCase;
        this.editFeedEntryUseCase = editFeedEntryUseCase;
        this.addEntryToFeedUseCase = addEntryToFeedUseCase;
    }

    @PostMapping("/feeds/start")
    public ResponseEntity<FeedResponse> start(@PathVariable Long babyId, @Valid @RequestBody StartFeedEntryRequest request, Authentication auth) {
        Feed feed = startFeedEntryUseCase.startEntry(babyId, userId(auth), request.breastSide(), request.startTime());
        return ResponseEntity.status(HttpStatus.CREATED).body(FeedResponse.from(feed));
    }

    @GetMapping("/feeds")
    public List<FeedResponse> list(@PathVariable Long babyId, Authentication auth) {
        return listFeedsUseCase.listForBaby(babyId, userId(auth)).stream().map(FeedResponse::from).toList();
    }

    @DeleteMapping("/feeds/{feedId}/entries/{entryId}")
    public ResponseEntity<Void> deleteEntry(@PathVariable Long babyId, @PathVariable Long feedId, @PathVariable Long entryId, Authentication auth) {
        deleteFeedEntryUseCase.deleteEntry(babyId, userId(auth), feedId, entryId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stats/today")
    public StatsResponse statsToday(@PathVariable Long babyId, Authentication auth) {
        return StatsResponse.from(getStatsUseCase.getStats(babyId, userId(auth), LocalDate.now()));
    }

    @GetMapping("/feeds/range")
    public List<FeedResponse> range(@PathVariable Long babyId, @RequestParam Instant from, @RequestParam Instant to, Authentication auth) {
        return getFeedsInRangeUseCase.getForBabyInRange(babyId, userId(auth), from, to).stream().map(FeedResponse::from).toList();
    }

    @PostMapping("/feeds/manual")
    public ResponseEntity<FeedResponse> recordManual(@PathVariable Long babyId, @Valid @RequestBody RecordManualFeedRequest request, Authentication auth) {
        List<RecordManualFeedUseCase.SideEntry> entries = request.entries().stream()
                .map(e -> new RecordManualFeedUseCase.SideEntry(e.breastSide(), e.startTime(), e.endTime()))
                .toList();
        Feed feed = recordManualFeedUseCase.record(babyId, userId(auth), entries);
        return ResponseEntity.status(HttpStatus.CREATED).body(FeedResponse.from(feed));
    }

    @PostMapping("/feeds/{feedId}/entries/{entryId}/pause")
    public FeedResponse pause(@PathVariable Long babyId, @PathVariable Long feedId, @PathVariable Long entryId,
                              @RequestBody(required = false) StopFeedEntryRequest request, Authentication auth) {
        Instant pauseTime = request != null ? request.endTime() : null;
        return FeedResponse.from(pauseFeedEntryUseCase.pauseEntry(babyId, userId(auth), feedId, entryId, pauseTime));
    }

    @PostMapping("/feeds/{feedId}/entries/{entryId}/resume")
    public FeedResponse resume(@PathVariable Long babyId, @PathVariable Long feedId, @PathVariable Long entryId,
                               @RequestBody(required = false) StopFeedEntryRequest request, Authentication auth) {
        Instant resumeTime = request != null ? request.endTime() : null;
        return FeedResponse.from(resumeFeedEntryUseCase.resumeEntry(babyId, userId(auth), feedId, entryId, resumeTime));
    }

    @PostMapping("/feeds/{feedId}/finish")
    public FeedResponse finish(@PathVariable Long babyId, @PathVariable Long feedId,
                               @RequestBody(required = false) StopFeedEntryRequest request, Authentication auth) {
        Instant finishTime = request != null ? request.endTime() : null;
        return FeedResponse.from(finishedFeedEntryUseCase.finish(babyId, userId(auth), feedId, finishTime));
    }

    @PatchMapping("/feeds/{feedId}/entries/{entryId}")
    public FeedResponse editEntry(@PathVariable Long babyId, @PathVariable Long feedId, @PathVariable Long entryId,
                                  @Valid @RequestBody EditFeedEntryRequest request, Authentication auth) {
        Feed feed = editFeedEntryUseCase.editEntry(babyId, userId(auth), feedId, entryId, request.startTime(), request.endTime());
        return FeedResponse.from(feed);
    }

    @PostMapping("/feeds/{feedId}/entries")
    public FeedResponse addEntry(@PathVariable Long babyId, @PathVariable Long feedId,
                                 @Valid @RequestBody ManualBreastSideEntry request, Authentication auth) {
        Feed feed = addEntryToFeedUseCase.addEntry(babyId, userId(auth), feedId, request.breastSide(), request.startTime(), request.endTime());
        return FeedResponse.from(feed);
    }

    private Long userId(Authentication auth) {
        return Long.parseLong(auth.getName());
    }
}
