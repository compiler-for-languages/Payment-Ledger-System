import { useEffect } from "react";
import { PageHeader } from "../components/PageHeader";
import { SimpleTable } from "../components/SimpleTable";
import { StatItem } from "../components/StatItem";
import { useTransactionStore } from "../store/transactionStore";
import { useWalletStore } from "../store/walletStore";
import { formatCurrency, formatDateTime } from "../utils/format";

export function UserDashboardPage() {
  const wallet = useWalletStore((state) => state.wallet);
  const transactions = useTransactionStore((state) => state.transactions);

  useEffect(() => {
    // Call via getState() so action references are never reactive dependencies.
    // Each store's loading guard prevents duplicate concurrent calls.
    useWalletStore.getState().loadMyWallet();
    useTransactionStore.getState().loadTransactions();
  }, []);

  const todayTransactions = transactions.filter(t => {
    const today = new Date();
    const txnDate = new Date(t.createdAt);
    return txnDate.toDateString() === today.toDateString();
  });

  const monthlyVolume = transactions.reduce((sum, t) => {
    const now = new Date();
    const txnDate = new Date(t.createdAt);
    if (txnDate.getMonth() === now.getMonth() && txnDate.getFullYear() === now.getFullYear()) {
      return sum + t.amount;
    }
    return sum;
  }, 0);

  return (
    <div className="space-y-6" style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
      <PageHeader title="User Dashboard" description="Wallet health, recent transactions, and ledger visibility." />
      <div className="grid gap-4 sm:grid-cols-3" style={{ display: 'grid', gap: '1rem', gridTemplateColumns: 'repeat(auto-fit, minmax(280px, 1fr))' }}>
        <StatItem
          label="Wallet Balance"
          value={formatCurrency(wallet?.balance ?? 0)}
          trend="Available Funds"
          color="var(--enterprise-primary)"
        />
        <StatItem
          label="Wallet Status"
          value={wallet?.status ?? "ACTIVE"}
          trend={wallet?.status === "ACTIVE" ? "Operational" : "Restricted"}
          color={wallet?.status === "ACTIVE" ? "var(--enterprise-success)" : "var(--enterprise-warning)"}
        />
        <StatItem
          label="Today's Transactions"
          value={String(todayTransactions.length)}
          trend="Activity Today"
          color="var(--enterprise-info)"
        />
        <StatItem
          label="Monthly Volume"
          value={formatCurrency(monthlyVolume)}
          trend="This Month"
          color="var(--enterprise-primary)"
        />
        <StatItem
          label="Ledger Entries"
          value={String(transactions.length)}
          trend="Total Records"
          color="var(--enterprise-text-muted)"
        />
        <StatItem
          label="Risk Status"
          value="LOW"
          trend="Security Level"
          color="var(--enterprise-success)"
        />
      </div>
      <SimpleTable
        headers={["Reference", "Type", "Status", "Amount", "Timestamp"]}
        rows={transactions.map((txn) => [
          txn.referenceNumber,
          txn.type,
          txn.status,
          formatCurrency(txn.amount),
          formatDateTime(txn.createdAt),
        ])}
      />
    </div>
  );
}
