export type UserRole = "ADMIN" | "USER";

export interface AuthUser {
  id: number;
  fullName: string;
  email: string;
  username: string;
  role: UserRole;
}

export interface AuthResponse {
  token: string;
  user: AuthUser;
}

export interface RegisterPayload {
  fullName: string;
  email: string;
  username: string;
  password: string;
}
