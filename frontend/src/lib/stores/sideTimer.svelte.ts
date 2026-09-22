import type { FeedResponse, BreastSide } from "#lib/types/feed.ts";
import { startFeedWithRetry, stopFeedWithRetry } from "#lib/services/feedService.ts";

export function createSideTimer(breastSide: BreastSide) {
  let feed = $state<FeedResponse | null>(null);
  let loading = $state(false);
  let syncError = $state(false);

  return {
    get current() { return feed; },
    get isLoading() { return loading; },
    get hasSyncError() { return syncError; },

    setFromServer(f: FeedResponse | null) {
      feed = f;
    },

    async start(babyId: number) {
      const clientStartTime = new Date().toISOString();
      feed = { id: -1, babyId, breastSide, startTime: clientStartTime, endTime: null, ongoing: true, durationMinutes: null };
      loading = true;
      syncError = false;
      try {
        feed = await startFeedWithRetry(babyId, breastSide, clientStartTime);
      } catch {
        syncError = true;
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
}