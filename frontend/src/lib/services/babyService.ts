import { apiFetch } from "#lib/services/clientService.ts";
import type { BabyResponse } from "#lib/types/baby.ts";

const API_BABY_URI = import.meta.env.VITE_API_BABY_URI ?? "/api/v1/babies";

export function listBabies(): Promise<BabyResponse[]> {
  return apiFetch<BabyResponse[]>(`${API_BABY_URI}`);
}

export function createBaby(name: string, birthDate: string | null): Promise<BabyResponse> {
  return apiFetch<BabyResponse>(`${API_BABY_URI}`, {
    method: "POST",
    body: JSON.stringify({ name, birthDate }),
  });
}