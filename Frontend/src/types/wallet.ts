export type WalletStatus = "ACTIVE" | "FROZEN" | "BLOCKED";

export interface Wallet {
  id: number;
  userId: number;
  balance: number;
  status: WalletStatus;
  createdAt: string;
  updatedAt: string;
}
