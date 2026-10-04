import { ROOT_BASE } from "#lib/constants.ts";
import { authentication } from "#lib/stores/auth.svelte.ts";

async function executeCall(path: string, options: RequestInit): Promise<Response> {
  return fetch(`${ROOT_BASE}${path}`, {
    ...options,
    headers: {
      "Content-Type": "application/json",
      ...(authentication.accessToken ? { Authorization: `Bearer ${authentication.accessToken}` } : {}),
      ...options.headers,
    },
  });
}

export async function apiFetch<T>(path: string, options: RequestInit = {}): Promise<T> {
    let response = await executeCall(path, options);
    if (response.status === 401) {
        const isRefreshed = await authentication.tryRefresh();
        if (isRefreshed) {
            response = await executeCall(path, options);
        }
    }
    if (!response.ok) {
        const body = await response.json().catch(() => ({}));
        throw new Error(body.error ?? `Error ${response.status}`);
    }
    return response.status === 204 ? (undefined as T) : response.json();
}