import { apiFetch } from "./clientService";
import type { FeedResponse, BreastSide } from "#lib/types/feed.ts";

const API_URI: string = import.meta.env.VITE_API_URI ?? "/api/v1/babies";

async function withRetry<T>(fn: () => Promise<T>, delayMs = 5000): Promise<T> {
  while (true) {
    try {
      return await fn();
    } catch {
      await new Promise((resolve) => setTimeout(resolve, delayMs));
    }
  }
}

export function startFeedWithRetry(babyId: number, breastSide: BreastSide, startTime: string): Promise<FeedResponse> {
  return withRetry(() => startFeed(babyId, breastSide, startTime));
}

export function stopFeedWithRetry(babyId: number, id: number, endTime: string): Promise<FeedResponse> {
  return withRetry(() => stopFeed(babyId, id, endTime));
}

export function startFeed(babyId: number, breastSide: BreastSide, startTime: string): Promise<FeedResponse> {
  return apiFetch<FeedResponse>(`${API_URI}/${babyId}/feeds/start`, {
    method: "POST",
    body: JSON.stringify({ breastSide, startTime }),
  });
}

export function stopFeed(babyId: number, id: number, endTime: string): Promise<FeedResponse> {
  return apiFetch<FeedResponse>(`${API_URI}/${babyId}/feeds/${id}/stop`, { 
    method: "POST",
    body: JSON.stringify({ endTime }) 
  });
}

export function listFeeds(babyId: number): Promise<FeedResponse[]> {
  return apiFetch<FeedResponse[]>(`${API_URI}/${babyId}/feeds`);
}

export function deleteFeed(babyId: number, id: number): Promise<void> {
  return apiFetch<void>(`${API_URI}/${babyId}/feeds/${id}`, { method: "DELETE" });
}

export function listFeedsInRange(babyId: number, from: string, to: string): Promise<FeedResponse[]> {
  const params = new URLSearchParams({ from, to });
  return apiFetch<FeedResponse[]>(`${API_URI}/${babyId}/feeds/range?${params}`);
}