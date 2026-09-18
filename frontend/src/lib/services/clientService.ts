const API_BASE = import.meta.env.VITE_API_BASE ?? "http://localhost:8080/api/v1";
const API_KEY = import.meta.env.VITE_API_KEY ?? "c69712dd20875b58b75cc85f70dc606e315493f3808dd45b9d3f05aae5d8568b";

export async function apiFetch<T>(path: string, options: RequestInit = {}): Promise<T> {
  const res = await fetch(`${API_BASE}${path}`, {
    ...options,
    headers: {
      "Content-Type": "application/json",
      "X-API-Key": API_KEY,
      ...options.headers,
    },
  });

  if (!res.ok) {
    const body = await res.json().catch(() => ({}));
    throw new Error(body.error ?? `Erreur ${res.status}`);
  }

  return res.status === 204 ? (undefined as T) : res.json();
}