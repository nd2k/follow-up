import { apiFetch } from "./clientService";
import type { FeedResponse, BreastSide } from "#lib/types/feed.ts";

const API_URI: string = import.meta.env.VITE_API_URI ?? "/api/v1/feeds";

async function withRetry<T>(fn: () => Promise<T>, delayMs = 5000): Promise<T> {
  while (true) {
    try {
      return await fn();
    } catch {
      await new Promise((resolve) => setTimeout(resolve, delayMs));
    }
  }
}

export function startFeedWithRetry(breastSide: BreastSide, startTime: string): Promise<FeedResponse> {
  return withRetry(() => startFeed(breastSide, startTime));
}

export function stopFeedWithRetry(id: number): Promise<FeedResponse> {
  return withRetry(() => stopFeed(id));
}

export function startFeed(breastSide: BreastSide, startTime: string): Promise<FeedResponse> {
  return apiFetch<FeedResponse>(`${API_URI}/start`, {
    method: "POST",
    body: JSON.stringify({ breastSide, startTime }),
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