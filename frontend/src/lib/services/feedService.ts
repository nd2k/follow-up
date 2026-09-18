import { apiFetch } from "./clientService";
import type { FeedResponse, BreastSide } from "#lib/types/feed.ts";

export function startFeed(breastSide: BreastSide): Promise<FeedResponse> {
  return apiFetch<FeedResponse>("/feeds/start", {
    method: "POST",
    body: JSON.stringify({ breastSide }),
  });
}

export function stopFeed(id: number): Promise<FeedResponse> {
  return apiFetch<FeedResponse>(`/feeds/${id}/stop`, { method: "POST" });
}

export function listFeeds(): Promise<FeedResponse[]> {
  return apiFetch<FeedResponse[]>("/feeds");
}

export function deleteFeed(id: number): Promise<void> {
  return apiFetch<void>(`/feeds/${id}`, { method: "DELETE" });
}