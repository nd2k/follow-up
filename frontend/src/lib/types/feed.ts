export type BreastSide = "LEFT" | "RIGHT";

export interface FeedResponse {
  id: number;
  babyId: number;
  sessionId: number;
  breastSide: BreastSide;
  startTime: string;
  endTime: string | null;
  ongoing: boolean;
  durationMinutes: number | null;
}

export interface StatsResponse {
  count: number;
  totalDurationInMinutes: number;
  averageInMinutes: number;
}