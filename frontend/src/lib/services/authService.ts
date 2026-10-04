import type { AuthTokens } from "#lib/types/auth.ts";
import { apiFetch } from "./clientService";


export async function refreshTokens(refreshToken: string): Promise<AuthTokens> {
    return apiFetch(`/auth/v1/refresh`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ refreshToken })
    })
}

export async function login(email: string, password: string): Promise<AuthTokens> {
    return apiFetch(`/auth/v1/login`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email, password })
    });
}