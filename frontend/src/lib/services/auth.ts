import type { AuthTokens } from "#lib/types/auth.ts";

const ROOT_BASE = import.meta.env.VITE_API_ROOT ?? "http://localhost:8080";

async function handle(res: Response): Promise<AuthTokens> {
  if (!res.ok) {
    const body = await res.json().catch(() => ({}));
    throw new Error(body.error ?? "Échec de connexion");
  }
  return res.json();
}

export async function login(email: string, password: string): Promise<AuthTokens> {
  return fetch(`${ROOT_BASE}/auth/v1/login`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ email, password }),
  }).then(handle);
}

export async function refreshTokens(refreshToken: string): Promise<AuthTokens> {
  return fetch(`${ROOT_BASE}/auth/v1/refresh`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ refreshToken }),
  }).then(handle);
}