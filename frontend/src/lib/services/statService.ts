import { apiFetch } from "./clientService";
import type { StatsResponse } from "#lib/types/feed.ts";

export function getStatsToday(): Promise<StatsResponse> {
  return apiFetch<StatsResponse>("/stats/today");
}