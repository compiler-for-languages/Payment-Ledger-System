import { Transaction, LedgerEntry } from "../types/transaction";
import { Wallet } from "../types/wallet";

export const wallets: Wallet[] = [
  { id: 101, userId: 2, balance: 89250, status: "ACTIVE", createdAt: "2026-07-08T10:32:00", updatedAt: "2026-07-08T10:32:00" },
  { id: 102, userId: 8, balance: 14500, status: "FROZEN", createdAt: "2026-07-08T10:32:00", updatedAt: "2026-07-08T08:11:00" },
  { id: 103, userId: 12, balance: 5600, status: "ACTIVE", createdAt: "2026-07-08T10:32:00", updatedAt: "2026-07-08T09:12:00" },
];

export const transactions: Transaction[] = [
  {
    id: 7001,
    referenceNumber: "TXN-20260708-0001",
    type: "TRANSFER",
    status: "SUCCESS",
    amount: 5000,
    senderWalletId: 101,
    receiverWalletId: 103,
    description: "Settlement transfer",
    createdAt: "2026-07-08T09:25:00",
  },
  {
    id: 7002,
    referenceNumber: "TXN-20260708-0002",
    type: "WITHDRAWAL",
    status: "FAILED",
    amount: 99000,
    senderWalletId: 101,
    description: "Insufficient balance check",
    createdAt: "2026-07-08T09:51:00",
  },
  {
    id: 7003,
    referenceNumber: "TXN-20260708-0003",
    type: "DEPOSIT",
    status: "SUCCESS",
    amount: 7500,
    receiverWalletId: 101,
    description: "Wallet top-up",
    createdAt: "2026-07-08T10:02:00",
  },
];

export const ledgerEntries: LedgerEntry[] = [
  {
    id: 9001,
    transactionId: 7001,
    walletId: 101,
    entryType: "DEBIT",
    amount: 5000,
    narration: "Transfer to wallet 103",
    createdAt: "2026-07-08T09:25:02",
  },
  {
    id: 9002,
    transactionId: 7001,
    walletId: 103,
    entryType: "CREDIT",
    amount: 5000,
    narration: "Transfer received from wallet 101",
    createdAt: "2026-07-08T09:25:02",
  },
  {
    id: 9003,
    transactionId: 7003,
    walletId: 101,
    entryType: "CREDIT",
    amount: 7500,
    narration: "Deposit posted",
    createdAt: "2026-07-08T10:02:11",
  },
];
