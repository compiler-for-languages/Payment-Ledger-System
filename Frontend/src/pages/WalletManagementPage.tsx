import { useEffect } from "react";
import { PageHeader } from "../components/PageHeader";
import { SimpleTable } from "../components/SimpleTable";
import { useWalletStore } from "../store/walletStore";
import { formatCurrency } from "../utils/format";

export function WalletManagementPage() {
  const allWallets = useWalletStore((state) => state.allWallets);

  useEffect(() => {
    // Call via getState() so the action reference is never a reactive dependency.
    useWalletStore.getState().loadAllWallets();
  }, []);

  return (
    <div className="space-y-6" style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
      <PageHeader title="Wallet Management" description="Freeze, activate, lock, and inspect enterprise wallets." />
      <SimpleTable
        headers={["Wallet ID", "User ID", "Balance", "Status"]}
        rows={allWallets.map((wallet) => [
          String(wallet.id),
          String(wallet.userId),
          formatCurrency(wallet.balance),
          wallet.status,
        ])}
      />
    </div>
  );
}
