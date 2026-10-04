import { API_URI } from "#lib/constants.ts";
import type { BreastSide, FeedResponse, ManualSideEntry } from "#lib/types/feed.ts";
import { apiFetch } from "./clientService";

export function listOfFeeds(babyId: number): Promise<FeedResponse[]> {
  return apiFetch<FeedResponse[]>(`${API_URI}/${babyId}/feeds`);
}

export function startFeedEntry(babyId: number, breastSide: BreastSide, startTime: string): Promise<FeedResponse> {
  return apiFetch<FeedResponse>(`${API_URI}/${babyId}/feeds/start`, {
    method: "POST",
    body: JSON.stringify({ breastSide, startTime }),
  });
}

export function pauseFeedEntry(babyId: number, feedId: number, entryId: number, pauseTime: string): Promise<FeedResponse> {
  return apiFetch<FeedResponse>(`${API_URI}/${babyId}/feeds/${feedId}/entries/${entryId}/pause`, {
    method: "POST",
    body: JSON.stringify({ endTime: pauseTime }),
  });
}

export function resumeFeedEntry(babyId: number, feedId: number, entryId: number, resumeTime: string): Promise<FeedResponse> {
  return apiFetch<FeedResponse>(`${API_URI}/${babyId}/feeds/${feedId}/entries/${entryId}/resume`, {
    method: "POST",
    body: JSON.stringify({ endTime: resumeTime }),
  });
}

export function deleteFeedEntry(babyId: number, feedId: number, entryId: number): Promise<void> {
  return apiFetch<void>(`${API_URI}/${babyId}/feeds/${feedId}/entries/${entryId}`, { method: "DELETE" });
}

export function listFeedsInRange(babyId: number, from: string, to: string): Promise<FeedResponse[]> {
  const params = new URLSearchParams({ from, to });
  return apiFetch<FeedResponse[]>(`${API_URI}/${babyId}/feeds/range?${params}`);
}

export function recordManualFeed(babyId: number, entries: ManualSideEntry[]): Promise<FeedResponse> {
  return apiFetch<FeedResponse>(`${API_URI}/${babyId}/feeds/manual`, {
    method: "POST",
    body: JSON.stringify({ entries }),
  });
}

export function finishFeed(babyId: number, feedId: number, finishTime: string): Promise<FeedResponse> {
  return apiFetch<FeedResponse>(`${API_URI}/${babyId}/feeds/${feedId}/finish`, {
    method: "POST",
    body: JSON.stringify({ endTime: finishTime }),
  });
}

async function withRetry<T>(fn: () => Promise<T>, maxAttempts = 20, delayMs = 5000): Promise<T> {
  let lastError: unknown;
  for (let attempt = 1; attempt <= maxAttempts; attempt++) {
    try { return await fn(); }
    catch (err) {
      lastError = err;
      if (attempt === maxAttempts) break;
      await new Promise((r) => setTimeout(r, delayMs));
    }
  }
  throw lastError;
}

export function startFeedEntryWithRetry(babyId: number, breastSide: BreastSide, startTime: string) {
  return withRetry(() => startFeedEntry(babyId, breastSide, startTime));
}
export function pauseFeedEntryWithRetry(babyId: number, feedId: number, entryId: number, pauseTime: string) {
  return withRetry(() => pauseFeedEntry(babyId, feedId, entryId, pauseTime));
}
export function resumeFeedEntryWithRetry(babyId: number, feedId: number, entryId: number, resumeTime: string) {
  return withRetry(() => resumeFeedEntry(babyId, feedId, entryId, resumeTime));
}

export function finishFeedWithRetry(babyId: number, feedId: number, finishTime: string) {
  return withRetry(() => finishFeed(babyId, feedId, finishTime));
}

export function editFeedEntry(babyId: number, feedId: number, entryId: number, startTime: string, endTime: string): Promise<FeedResponse> {
  return apiFetch<FeedResponse>(`${API_URI}/${babyId}/feeds/${feedId}/entries/${entryId}`, {
    method: "PATCH",
    body: JSON.stringify({ startTime, endTime }),
  });
}

export function addEntryToFeed(babyId: number, feedId: number, breastSide: BreastSide, startTime: string, endTime: string): Promise<FeedResponse> {
  return apiFetch<FeedResponse>(`${API_URI}/${babyId}/feeds/${feedId}/entries`, {
    method: "POST",
    body: JSON.stringify({ breastSide, startTime, endTime }),
  });
}