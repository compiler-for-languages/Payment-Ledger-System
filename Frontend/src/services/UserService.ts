import { apiClient } from "./apiClient";
import { AuthUser } from "../types/auth";

class UserService {
  async updateProfile(payload: { fullName: string; username: string }): Promise<AuthUser> {
    const response = await apiClient.patch<AuthUser>("/users/me", payload);
    return response.data;
  }

  async changePassword(payload: any): Promise<void> {
    await apiClient.patch("/users/me/password", payload);
  }
}

export const userService = new UserService();
