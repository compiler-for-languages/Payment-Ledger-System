import { apiClient } from "./apiClient";
import { AuthUser } from "../types/auth";
import { Wallet } from "../types/wallet";
import { Transaction, TransactionSummary } from "../types/transaction";

class AdminService {
  async getDashboardStats(): Promise<any> {
    const response = await apiClient.get("/admin/dashboard");
    return response.data;
  }

  async getTransactionSummary(from?: string, to?: string): Promise<TransactionSummary> {
    const response = await apiClient.get<TransactionSummary>("/admin/reports/summary", {
      params: { from, to }
    });
    return response.data;
  }

  async getAllUsers(): Promise<AuthUser[]> {
    const response = await apiClient.get("/admin/users");
    return response.data;
  }

  async getAllWallets(): Promise<Wallet[]> {
    const response = await apiClient.get("/admin/wallets");
    return response.data;
  }

  async getAllTransactions(): Promise<Transaction[]> {
    const response = await apiClient.get("/admin/transactions");
    return response.data;
  }

  async getAuditLogs(): Promise<any[]> {
    const response = await apiClient.get("/admin/audit-logs");
    return response.data;
  }
}

export const adminService = new AdminService();
