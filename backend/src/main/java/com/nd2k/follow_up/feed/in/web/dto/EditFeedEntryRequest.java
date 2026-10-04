package com.nd2k.follow_up.feed.in.web.dto;

import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record EditFeedEntryRequest(@NotNull Instant startTime, @NotNull Instant endTime) {}
