import { create } from "zustand";
import { walletService } from "../services/WalletService";
import { Wallet } from "../types/wallet";

// Test data for development
const mockWallet: Wallet = {
  id: 1,
  userId: 1,
  balance: 25000.00,
  status: "ACTIVE",
  createdAt: new Date("2024-01-15T10:30:00Z").toISOString(),
  updatedAt: new Date().toISOString(),
};

const mockAllWallets: Wallet[] = [
  { id: 1, userId: 1, balance: 25000.00, status: "ACTIVE", createdAt: new Date("2024-01-15T10:30:00Z").toISOString(), updatedAt: new Date().toISOString() },
  { id: 2, userId: 2, balance: 15000.00, status: "ACTIVE", createdAt: new Date("2024-02-20T14:15:00Z").toISOString(), updatedAt: new Date().toISOString() },
  { id: 3, userId: 3, balance: 50000.00, status: "FROZEN", createdAt: new Date("2024-03-10T09:00:00Z").toISOString(), updatedAt: new Date().toISOString() },
];

interface WalletState {
  wallet: Wallet | null;
  allWallets: Wallet[];
  loading: boolean;
  loadingMyWallet: boolean;
  loadingAllWallets: boolean;

  // Actions
  loadMyWallet: () => Promise<void>;
  loadAllWallets: () => Promise<void>;
  clearWallet: () => void;
}

export const useWalletStore = create<WalletState>((set, get) => ({
  wallet: null,
  allWallets: [],
  loading: false,
  loadingMyWallet: false,
  loadingAllWallets: false,

  loadMyWallet: async () => {
    // Guard: do not fire a second request while one is already in-flight.
    if (get().loadingMyWallet) return;
    set({ loadingMyWallet: true });
    try {
      const wallet = await walletService.getMyWallet();
      set({ wallet, loadingMyWallet: false });
    } catch (error) {
      // Use mock data for development/testing
      console.log("Using mock wallet data for development");
      set({ wallet: mockWallet, loadingMyWallet: false });
    }
  },

  loadAllWallets: async () => {
    if (get().loadingAllWallets) return;
    set({ loadingAllWallets: true });
    try {
      const allWallets = await walletService.getAllWallets();
      set({ allWallets, loadingAllWallets: false });
    } catch (error) {
      // Use mock data for development/testing
      console.log("Using mock wallets data for development");
      set({ allWallets: mockAllWallets, loadingAllWallets: false });
    }
  },

  clearWallet: () => set({ wallet: null, allWallets: [] }),
}));
