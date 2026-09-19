import { apiFetch } from "./clientService.ts";

export function checkHealth(): Promise<void> {
  return apiFetch<void>("/health");
}