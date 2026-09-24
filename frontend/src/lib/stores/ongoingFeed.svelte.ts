import { createSideTimer } from "./sideTimer.svelte.ts";
import { listFeeds } from "#lib/services/feedService.ts";
import type { BreastSide } from "#lib/types/feed.ts";

export const leftTimer = createSideTimer("LEFT");
export const rightTimer = createSideTimer("RIGHT");

export async function initOngoingFeeds(babyId: number) {
  const feeds = await listFeeds(babyId);
  const ongoing = feeds.filter((f) => f.ongoing);
  leftTimer.setFromServer(ongoing.find((f) => f.breastSide === "LEFT") ?? null);
  rightTimer.setFromServer(ongoing.find((f) => f.breastSide === "RIGHT") ?? null);
}

export async function startSide(babyId: number, breastSide: BreastSide) {
  const other = breastSide === "LEFT" ? rightTimer : leftTimer;
  const target = breastSide === "LEFT" ? leftTimer : rightTimer;
  if (other.current && !other.isPendingSave) {
    other.stopClock();
  }
  await target.start(babyId);
}