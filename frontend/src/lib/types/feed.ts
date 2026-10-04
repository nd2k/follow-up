export type BreastSide = "LEFT" | "RIGHT";

export interface FeedEntryResponse {
  id: number;
  breastSide: BreastSide;
  startTime: string;
  endTime: string;
  ongoing: boolean;
  durationSeconds: number;
  durationMinutes: number;
}

export interface FeedResponse {
  id: number;
  babyId: number;
  startTime: string;
  endTime: string | null;
  ongoing: boolean;
  entries: FeedEntryResponse[];
}

export interface StatsResponse {
  count: number;
  totalDurationInMinutes: number;
  averageInMinutes: number;
}

export interface SessionRow {
    dayKey: string;
    sessionId: number;
    startTime: string;
    endTime: string;
    feeds: FeedResponse[];
    delayToNext: number | null; 
}

export interface DayGroup {
    key: string; 
    feeds: FeedResponse[]; 
    totalMinutes: number; 
    count: number;
}

export interface ManualSideEntry { 
  breastSide: BreastSide; 
  startTime: string; 
  endTime: string; 
}
