import { listOfFeeds } from "#lib/services/feedService.ts";
import type { BreastSide, FeedResponse, FeedEntryResponse } from "#lib/types/feed.ts";
import { startFeedEntryWithRetry, pauseFeedEntryWithRetry, resumeFeedEntryWithRetry, finishFeedWithRetry } from "#lib/services/feedService.ts";

let activeFeed = $state<FeedResponse | null>(null);
let loading = $state(false);
let syncError = $state(false);

function entryFor(breastSide: BreastSide): FeedEntryResponse | null {
  return activeFeed?.entries.find((e) => e.breastSide === breastSide) ?? null;
}

export const feedSession = {
    get isLoading() { return loading; },
    get hasSyncError() { return syncError; },
    canFinish() { return activeFeed !== null; },

    entry(breastSide: BreastSide) { return entryFor(breastSide); },
    isRunning(breastSide: BreastSide) {         
        return entryFor(breastSide)?.ongoing ?? false; 
    },
    isPaused(breastSide: BreastSide) {
        const entry = entryFor(breastSide);
        return entry === null ? false :!entry.ongoing;
    },
    setFromServer(feed: FeedResponse | null) {
        activeFeed = feed;
    },
    async toggleChrono(babyId: number, breastSide: BreastSide) {
        loading = true;
        syncError = false;
        const clientStartTime = new Date().toISOString();
        try {
            const otherChrono: BreastSide = breastSide === "LEFT" ? "RIGHT" : "LEFT";
            if (this.isRunning(otherChrono) && activeFeed) {
                const otherEntry = entryFor(otherChrono)!;
                activeFeed = await pauseFeedEntryWithRetry(babyId, activeFeed!.id, otherEntry.id, clientStartTime);
            }
            const existingEntry = entryFor(breastSide);
            if (existingEntry?.ongoing) {
                activeFeed = await pauseFeedEntryWithRetry(babyId, activeFeed!.id, existingEntry.id, clientStartTime);
            } else if (existingEntry && !existingEntry.ongoing) {
                activeFeed = await resumeFeedEntryWithRetry(babyId, activeFeed!.id, existingEntry.id, clientStartTime);
            } else {
                activeFeed = await startFeedEntryWithRetry(babyId, breastSide, clientStartTime);
            }
            
        } catch {
            syncError = true;
        } finally {
            loading = false;
        }
    },
    async finish(babyId: number) {
        if (!activeFeed) return;
        loading = true;
        try {
            await finishFeedWithRetry(babyId, activeFeed.id, new Date().toISOString());
            activeFeed = null;
            syncError = false;
        } catch {
            syncError = true;
        } finally {
            loading = false;
        }
    }
}


export async function initiateActiveFeed(babyId: number) {
    const feeds = await listOfFeeds(babyId);
    feedSession.setFromServer(feeds.find(f => f.ongoing) ?? null);
}