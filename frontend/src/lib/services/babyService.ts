import { apiFetch } from "#lib/services/clientService.ts";
import type { BabyResponse } from "#lib/types/baby.ts";
import { API_URI } from "#lib/constants.ts";

export function listBabies(): Promise<BabyResponse[]> {
  return apiFetch<BabyResponse[]>(`${API_URI}`);
}

export function createBaby(name: string, birthDate: string | null): Promise<BabyResponse> {
  return apiFetch<BabyResponse>(`${API_URI}`, {
    method: "POST",
    body: JSON.stringify({ name, birthDate }),
  });
}