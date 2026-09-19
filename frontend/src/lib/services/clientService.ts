import { auth } from "#lib/stores/auth.svelte.ts";

const API_BASE = import.meta.env.VITE_API_BASE ?? "http://localhost:8080";

async function rawFetch(path: string, options: RequestInit): Promise<Response> {
  return fetch(`${API_BASE}${path}`, {
    ...options,
    headers: {
      "Content-Type": "application/json",
      ...(auth.accessToken ? { Authorization: `Bearer ${auth.accessToken}` } : {}),
      ...options.headers,
    },
  });
}

export async function apiFetch<T>(path: string, options: RequestInit = {}): Promise<T> {
  let res = await rawFetch(path, options);
  console.log(res);
  
  if (res.status === 401) {
    const refreshed = await auth.tryRefresh();
    if (refreshed) {
      res = await rawFetch(path, options);
    }
  }
  if (!res.ok) {
    const body = await res.json().catch(() => ({}));
    throw new Error(body.error ?? `Erreur ${res.status}`);
  }
  return res.status === 204 ? (undefined as T) : res.json();
}

