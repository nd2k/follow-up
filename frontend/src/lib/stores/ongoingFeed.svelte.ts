import type { FeedResponse, BreastSide } from "#lib/types/feed.ts";
import { startFeed, stopFeed, listFeeds } from "#lib/services/feedService.ts";

let feed = $state<FeedResponse | null>(null);
let loading = $state(false);

export const ongoingFeed = {
  get current() { return feed; },
  get isLoading() { return loading; },

  async init() {
    loading = true;
    try {
      const feeds = await listFeeds();
      if (feeds !== null) {
        feed = feeds.find((f) => Boolean(f.ongoing)) ?? null;
      }
    } finally {
      loading = false;
    }
  },

  async start(breastSide: BreastSide) {
    const optimisticFeed: FeedResponse = {
      id: -1,
      breastSide,
      startTime: new Date().toISOString(),
      endTime: null,
      ongoing: true,
      durationMinutes: null,
    };
    feed = optimisticFeed;
    loading = true;
    try {
      feed = await startFeed(breastSide);
    }catch(error) {
      feed = null;
      throw error;
    } finally {
      loading = false;
    }
  },

  async stop() {
    if (!feed) return;
    loading = true;
    try {
      await stopFeed(feed.id);
      feed = null;
    } finally {
      loading = false;
    }
  },
};