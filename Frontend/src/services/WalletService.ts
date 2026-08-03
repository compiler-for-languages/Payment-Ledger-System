import { apiClient } from "./apiClient";
import { Wallet } from "../types/wallet";

class WalletService {
  async getMyWallet(): Promise<Wallet> {
    const response = await apiClient.get<Wallet>("/wallets/me");
    return response.data;
  }

  async getAllWallets(): Promise<Wallet[]> {
    const response = await apiClient.get<Wallet[]>("/admin/wallets");
    return response.data;
  }

  async freezeWallet(id: number): Promise<Wallet> {
    const response = await apiClient.patch<Wallet>(`/wallets/${id}/freeze`);
    return response.data;
  }

  async activateWallet(id: number): Promise<Wallet> {
    const response = await apiClient.patch<Wallet>(`/wallets/${id}/activate`);
    return response.data;
  }
}

export const walletService = new WalletService();
