package com.nd2k.follow_up.feed.in.web.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record RecordManualFeedRequest(@NotEmpty @Valid List<ManualSideEntryDto> entries) {}
