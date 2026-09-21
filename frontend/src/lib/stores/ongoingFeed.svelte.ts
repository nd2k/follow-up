import type { FeedResponse, BreastSide } from "#lib/types/feed.ts";
import { startFeed, stopFeedWithRetry, listFeeds, startFeedWithRetry } from "#lib/services/feedService.ts";

let feed = $state<FeedResponse | null>(null);
let loading = $state(false);
let syncError = $state(false);

export const ongoingFeed = {
  get current() { return feed; },
  get isLoading() { return loading; },
  get hasSyncError() { return syncError; },

  async init(babyId: number) {
    loading = true;
    try {
      const feeds = await listFeeds(babyId);
      if (feeds !== null) {
        feed = feeds.find((f) => Boolean(f.ongoing)) ?? null;
      }
    } finally {
      loading = false;
    }
  },

  async start(babyId: number, breastSide: BreastSide) {
    const clientStartTime = new Date().toISOString();
    feed = {
      id: -1,
      babyId,
      breastSide,
      startTime: clientStartTime,
      endTime: null,
      ongoing: true,
      durationMinutes: null,
    };
    loading = true;
    syncError = false;
    try {
      feed = await startFeed(babyId, breastSide, clientStartTime);
    }catch(error) {
      syncError = true;
      throw error;
    } finally {
      loading = false;
    }
  },

  async stop() {
    if (!feed) return;
    loading = true;
    try {
      if (feed.id === -1) {
        feed = await startFeedWithRetry(feed.babyId, feed.breastSide, feed.startTime);
      }
      await stopFeedWithRetry(feed.babyId, feed.id);
      feed = null;
      syncError = false;
    } catch {
      syncError = true;
    } finally {
      loading = false;
    }
  },
};