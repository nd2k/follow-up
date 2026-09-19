import { login as apiLogin, refreshTokens } from "#lib/services/auth.ts";

const REFRESH_TOKEN_KEY = "follow_up_refresh_token";

let accessToken = $state<string | null>(null);
let status = $state<"unknown" | "authenticated" | "unauthenticated">("unknown");

export const auth = {
  get accessToken() { return accessToken; },
  get status() { return status; },
  async init() {
    const storedRefreshToken = localStorage.getItem(REFRESH_TOKEN_KEY);
    console.log(`refresh token ${storedRefreshToken}`)
    if (!storedRefreshToken) {
      status = "unauthenticated";
      return;
    }
    try {
      const tokens = await refreshTokens(storedRefreshToken);
      accessToken = tokens.accessToken;
      localStorage.setItem(REFRESH_TOKEN_KEY, tokens.refreshToken);
      status = "authenticated";
    } catch {
      localStorage.removeItem(REFRESH_TOKEN_KEY);
      status = "unauthenticated";
    }
  },

  async login(email: string, password: string) {
    const tokens = await apiLogin(email, password);
    accessToken = tokens.accessToken;
    localStorage.setItem(REFRESH_TOKEN_KEY, tokens.refreshToken);
    status = "authenticated";
  },

  logout() {
    accessToken = null;
    localStorage.removeItem(REFRESH_TOKEN_KEY);
    status = "unauthenticated";
  },

  async tryRefresh(): Promise<boolean> {
    const storedRefreshToken = localStorage.getItem(REFRESH_TOKEN_KEY);
    if (!storedRefreshToken) return false;
    try {
      const tokens = await refreshTokens(storedRefreshToken);
      accessToken = tokens.accessToken;
      localStorage.setItem(REFRESH_TOKEN_KEY, tokens.refreshToken);
      return true;
    } catch {
      this.logout();
      return false;
    }
  },
};