import { apiFetch } from "./clientService";
import type { StatsResponse } from "#lib/types/feed.ts";

export function getStatsToday(babyId: number): Promise<StatsResponse> {
  return apiFetch<StatsResponse>(`/api/v1/babies/${babyId}/stats/today`);
}