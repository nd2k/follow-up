package com.nd2k.follow_up.feed.in.web.dto;

import com.nd2k.follow_up.feed.core.domain.BreastSide;
import jakarta.validation.constraints.NotNull;

public record FeedRequestDto(@NotNull BreastSide breastSide) {
}
