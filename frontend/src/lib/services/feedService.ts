import { apiFetch } from "./clientService";
import type { FeedResponse, BreastSide } from "#lib/types/feed.ts";

const API_URI: string = import.meta.env.VITE_API_BASE ?? "/api/v1/feeds";

export function startFeed(breastSide: BreastSide): Promise<FeedResponse> {
  return apiFetch<FeedResponse>(`${API_URI}/start`, {
    method: "POST",
    body: JSON.stringify({ breastSide }),
  });
}

export function stopFeed(id: number): Promise<FeedResponse> {
  return apiFetch<FeedResponse>(`${API_URI}/${id}/stop`, { method: "POST" });
}

export function listFeeds(): Promise<FeedResponse[]> {
  return apiFetch<FeedResponse[]>(`${API_URI}`);
}

export function deleteFeed(id: number): Promise<void> {
  return apiFetch<void>(`${API_URI}/${id}`, { method: "DELETE" });
}