package com.nd2k.follow_up.feed.in.web.dto;

import com.nd2k.follow_up.feed.core.domain.BreastSide;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record ManualSideEntryDto(@NotNull BreastSide breastSide, @NotNull Instant startTime, @NotNull Instant endTime) {}
