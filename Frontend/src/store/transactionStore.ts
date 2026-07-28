import { create } from "zustand";
import { transactionService } from "../services/TransactionService";
import { Transaction, LedgerEntry } from "../types/transaction";

// Test data for development
const mockTransactions: Transaction[] = [
  {
    id: 1,
    referenceNumber: "TXN-2024-001",
    type: "DEPOSIT",
    status: "SUCCESS",
    amount: 5000.00,
    receiverWalletId: 1,
    description: "Bank deposit",
    createdAt: new Date("2024-06-15T10:30:00Z").toISOString(),
  },
  {
    id: 2,
    referenceNumber: "TXN-2024-002",
    type: "WITHDRAWAL",
    status: "SUCCESS",
    amount: 2000.00,
    senderWalletId: 1,
    description: "Bank withdrawal",
    createdAt: new Date("2024-06-18T14:15:00Z").toISOString(),
  },
  {
    id: 3,
    referenceNumber: "TXN-2024-003",
    type: "TRANSFER",
    status: "SUCCESS",
    amount: 3000.00,
    senderWalletId: 1,
    receiverWalletId: 2,
    description: "Transfer to user wallet",
    createdAt: new Date("2024-06-20T09:00:00Z").toISOString(),
  },
  {
    id: 4,
    referenceNumber: "TXN-2024-004",
    type: "DEPOSIT",
    status: "PENDING",
    amount: 10000.00,
    receiverWalletId: 1,
    description: "Pending bank deposit",
    createdAt: new Date("2024-06-22T16:45:00Z").toISOString(),
  },
  {
    id: 5,
    referenceNumber: "TXN-2024-005",
    type: "TRANSFER",
    status: "FAILED",
    amount: 15000.00,
    senderWalletId: 1,
    receiverWalletId: 3,
    description: "Failed transfer - insufficient funds",
    createdAt: new Date("2024-06-25T11:20:00Z").toISOString(),
  },
];

const mockLedgerEntries: LedgerEntry[] = [
  {
    id: 1,
    transactionId: 1,
    walletId: 1,
    entryType: "CREDIT",
    amount: 5000.00,
    narration: "Deposit credited to wallet",
    createdAt: new Date("2024-06-15T10:30:00Z").toISOString(),
  },
  {
    id: 2,
    transactionId: 2,
    walletId: 1,
    entryType: "DEBIT",
    amount: 2000.00,
    narration: "Withdrawal debited from wallet",
    createdAt: new Date("2024-06-18T14:15:00Z").toISOString(),
  },
  {
    id: 3,
    transactionId: 3,
    walletId: 1,
    entryType: "DEBIT",
    amount: 3000.00,
    narration: "Transfer debited from wallet",
    createdAt: new Date("2024-06-20T09:00:00Z").toISOString(),
  },
];

interface TransactionState {
  transactions: Transaction[];
  ledgerEntries: LedgerEntry[];
  loading: boolean;
  loadingTransactions: boolean;
  loadingLedger: boolean;

  // Actions
  loadTransactions: () => Promise<void>;
  loadLedger: () => Promise<void>;
  clearTransactions: () => void;
}

export const useTransactionStore = create<TransactionState>((set, get) => ({
  transactions: [],
  ledgerEntries: [],
  loading: false,
  loadingTransactions: false,
  loadingLedger: false,

  loadTransactions: async () => {
    // Guard: do not fire a second request while one is already in-flight.
    if (get().loadingTransactions) return;
    set({ loadingTransactions: true });
    try {
      const data = await transactionService.getMyTransactions();
      set({ transactions: data, loadingTransactions: false });
    } catch (error) {
      // Use mock data for development/testing
      console.log("Using mock transactions data for development");
      set({ transactions: mockTransactions, loadingTransactions: false });
    }
  },

  loadLedger: async () => {
    if (get().loadingLedger) return;
    set({ loadingLedger: true });
    try {
      const data = await transactionService.getMyLedger();
      set({ ledgerEntries: data, loadingLedger: false });
    } catch (error) {
      // Use mock data for development/testing
      console.log("Using mock ledger data for development");
      set({ ledgerEntries: mockLedgerEntries, loadingLedger: false });
    }
  },

  clearTransactions: () => set({ transactions: [], ledgerEntries: [] }),
}));
