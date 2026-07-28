import { useEffect } from "react";
import { PageHeader } from "../components/PageHeader";
import { StatItem } from "../components/StatItem";
import { useWalletStore } from "../store/walletStore";
import { formatCurrency, formatDateTime } from "../utils/format";

export function WalletPage() {
  const wallet = useWalletStore((state) => state.wallet);

  useEffect(() => {
    // Call via getState() so the action reference is never a reactive dependency.
    useWalletStore.getState().loadMyWallet();
  }, []);

  return (
    <div className="space-y-6" style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
      <PageHeader title="Wallet" description="Current balance snapshot and operational state." />
      <div className="grid gap-4 sm:grid-cols-3" style={{ display: 'grid', gap: '1rem', gridTemplateColumns: 'repeat(auto-fit, minmax(280px, 1fr))' }}>
        <StatItem label="Wallet ID" value={String(wallet?.id ?? "-")} trend="Unique Identifier" />
        <StatItem label="Available Balance" value={formatCurrency(wallet?.balance ?? 0)} trend="Current Funds" color="var(--enterprise-primary)" />
        <StatItem label="Wallet Status" value={wallet?.status ?? "ACTIVE"} trend={wallet?.status === "ACTIVE" ? "Operational" : "Restricted"} color={wallet?.status === "ACTIVE" ? "var(--enterprise-success)" : "var(--enterprise-warning)"} />
      </div>
      <div style={{ padding: '1.5rem', border: '1px solid var(--enterprise-border)', borderRadius: '8px', background: 'var(--enterprise-card)' }}>
        <p style={{ fontSize: '0.875rem', color: 'var(--enterprise-text-muted)', marginBottom: '0.5rem' }}>Last Updated</p>
        <p style={{ fontSize: '1rem', color: 'var(--enterprise-text)' }}>{wallet ? formatDateTime(wallet.updatedAt) : "-"}</p>
      </div>
    </div>
  );
}
