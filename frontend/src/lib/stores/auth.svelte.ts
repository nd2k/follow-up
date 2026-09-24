import { refreshTokens, login } from "#lib/services/authService.ts";
import { AuthenticationStatus, type AuthTokens } from "#lib/types/auth.ts";

const REFRESH_TOKEN_KEY = "follow_up_refresh_token";

type AuthStatus = typeof AuthenticationStatus[keyof typeof AuthenticationStatus];

let accessToken = $state<string | null>(null);
let status = $state<AuthStatus>("UNKNOWN");

export const authentication = {
    get accessToken() { return accessToken; },
    get status() { return status; },

    manageTokens(tokens: AuthTokens) {
        accessToken = tokens.accessToken;
        localStorage.setItem(REFRESH_TOKEN_KEY, tokens.refreshToken);
        status = "AUTHENTICATED";
    },

    logout() {
        accessToken = null;
        localStorage.removeItem(REFRESH_TOKEN_KEY);
        status = "UNAUTHENTICATED";
    },

    async init() {
        const storedRefreshToken = localStorage.getItem(REFRESH_TOKEN_KEY);
        if (!storedRefreshToken) {
            status = "UNAUTHENTICATED";
            return;
        }
        try {
            const tokens = await refreshTokens(storedRefreshToken);
            this.manageTokens(tokens);
        } catch {
            this.logout();
        }
    },

    async login(email: string, password: string) {
        const tokens = await login(email, password);
        this.manageTokens(tokens);
    },

    async tryRefresh(): Promise<boolean> {
        const storedRefreshToken = localStorage.getItem(REFRESH_TOKEN_KEY);
        if (!storedRefreshToken) return false;
        try {
            const tokens = await refreshTokens(storedRefreshToken);
            this.manageTokens(tokens);
            return true;
        } catch {
            this.logout();
            return false;
        }
    }

}