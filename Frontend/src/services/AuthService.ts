import { apiClient } from "./apiClient";
import { AuthResponse, RegisterPayload, AuthUser } from "../types/auth";

class AuthService {
  async login(email: string, password: string): Promise<AuthResponse> {
    const response = await apiClient.post<AuthResponse>("/auth/login", { email, password });
    return response.data;
  }

  async register(payload: RegisterPayload): Promise<AuthResponse> {
    const response = await apiClient.post<AuthResponse>("/auth/register", payload);
    return response.data;
  }

  async getMe(): Promise<AuthUser> {
    const response = await apiClient.get<AuthUser>("/users/me");
    return response.data;
  }
}

export const authService = new AuthService();
