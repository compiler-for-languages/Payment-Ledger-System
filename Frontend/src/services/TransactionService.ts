import { apiClient } from "./apiClient";
import { Transaction, LedgerEntry } from "../types/transaction";

class TransactionService {
  async getMyTransactions(): Promise<Transaction[]> {
    const response = await apiClient.get<Transaction[]>("/transactions/me");
    return response.data;
  }

  async getMyLedger(): Promise<LedgerEntry[]> {
    const response = await apiClient.get<LedgerEntry[]>("/ledger/me");
    return response.data;
  }

  async deposit(amount: number, description: string): Promise<Transaction> {
    const response = await apiClient.post<Transaction>("/transactions/deposit", { amount, description });
    return response.data;
  }

  async withdraw(amount: number, description: string): Promise<Transaction> {
    const response = await apiClient.post<Transaction>("/transactions/withdraw", { amount, description });
    return response.data;
  }

  async transfer(payload: { receiverUserId: number; amount: number; description: string; idempotencyKey: string }): Promise<Transaction> {
    const { idempotencyKey, ...data } = payload;
    const response = await apiClient.post<Transaction>("/transactions/transfer", data, {
      headers: { "X-Idempotency-Key": idempotencyKey }
    });
    return response.data;
  }
}

export const transactionService = new TransactionService();
