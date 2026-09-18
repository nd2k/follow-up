package com.nd2k.follow_up.feed.core.port.in;

import com.nd2k.follow_up.feed.core.domain.Feed;

import java.util.List;

public interface ListFeedsUseCase {

    List<Feed> listAllFeed();
}
