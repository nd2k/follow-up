export type BreastSide = "LEFT" | "RIGHT";

export interface FeedResponse {
  id: number;
  babyId: number;
  breastSide: BreastSide;
  startTime: string; // ISO instant
  endTime: string | null;
  ongoing: boolean;
  durationMinutes: number | null;
}

export interface StatsResponse {
  count: number;
  totalDurationInMinutes: number;
  averageInMinutes: number;
}