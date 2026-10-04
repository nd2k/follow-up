import type { StatsResponse } from "#lib/types/feed.ts";
import { apiFetch } from "./clientService";

export function getStatsToday(babyId: number): Promise<StatsResponse> {
  return apiFetch<StatsResponse>(`/api/v1/babies/${babyId}/stats/today`);
}