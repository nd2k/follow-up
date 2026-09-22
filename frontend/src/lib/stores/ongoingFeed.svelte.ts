import { createSideTimer } from "./sideTimer.svelte.ts";
import { listFeeds } from "#lib/services/feedService.ts";

export const leftTimer = createSideTimer("LEFT");
export const rightTimer = createSideTimer("RIGHT");

export async function initOngoingFeeds(babyId: number) {
  const feeds = await listFeeds(babyId);
  const ongoing = feeds.filter((f) => f.ongoing);
  leftTimer.setFromServer(ongoing.find((f) => f.breastSide === "LEFT") ?? null);
  rightTimer.setFromServer(ongoing.find((f) => f.breastSide === "RIGHT") ?? null);
}