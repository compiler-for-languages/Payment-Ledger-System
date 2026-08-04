export type TransactionType = "DEPOSIT" | "WITHDRAWAL" | "TRANSFER";
export type TransactionStatus = "PENDING" | "SUCCESS" | "FAILED" | "REVERSED";

export interface Transaction {
  id: number;
  referenceNumber: string;
  type: TransactionType;
  status: TransactionStatus;
  amount: number;
  senderWalletId?: number;
  receiverWalletId?: number;
  description: string;
  createdAt: string;
}

export interface LedgerEntry {
  id: number;
  transactionId: number;
  walletId: number;
  entryType: "CREDIT" | "DEBIT";
  amount: number;
  narration: string;
  createdAt: string;
}

export interface TransactionSummary {
  totalTransactions: number;
  successfulTransactions: number;
  failedTransactions: number;
  depositCount: number;
  withdrawalCount: number;
  transferCount: number;
  totalDepositAmount: number;
  totalWithdrawalAmount: number;
  totalTransferAmount: number;
}
