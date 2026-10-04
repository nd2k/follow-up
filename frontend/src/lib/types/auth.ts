export const AuthenticationStatus = {
    AUTHENTICATED: 'AUTHENTICATED',
    UNAUTHENTICATED: 'UNAUTHENTICATED',
    UNKNOWN: 'UNKNOWN'
} as const;

export interface AuthTokens {
  accessToken: string;
  refreshToken: string;
  username: string;
}