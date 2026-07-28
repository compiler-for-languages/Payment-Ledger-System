import { useEffect, useState } from "react";
import { PageHeader } from "../components/PageHeader";
import { StatItem } from "../components/StatItem";
import { adminService } from "../services/AdminService";
import { formatCurrency } from "../utils/format";

export function AdminDashboardPage() {
  const [stats, setStats] = useState<any>(null);

  useEffect(() => {
    adminService.getDashboardStats()
      .then(setStats)
      .catch(console.error);
  }, []);

  return (
    <div className="space-y-6" style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
      <PageHeader
        title="Admin Dashboard"
        description="Operational metrics for users, wallets, transaction success ratio, and risk visibility."
      />
      <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4" style={{ display: 'grid', gap: '1rem', gridTemplateColumns: 'repeat(auto-fit, minmax(250px, 1fr))' }}>
        <StatItem label="Total Users" value={String(stats?.totalUsers ?? 0)} trend="Registered Accounts" color="var(--enterprise-info)" />
        <StatItem label="Total Wallets" value={String(stats?.totalWallets ?? 0)} trend="Active Wallets" color="var(--enterprise-primary)" />
        <StatItem label="Total Balance" value={formatCurrency(stats?.totalBalance ?? 0)} trend="System Holdings" color="var(--enterprise-success)" />
        <StatItem label="Failed Transactions" value={String(stats?.failedTransactions ?? 0)} trend="Error Rate" color="var(--enterprise-danger)" />
        <StatItem label="Successful Transactions" value={String(stats?.successfulTransactions ?? 0)} trend="Completed" color="var(--enterprise-success)" />
        <StatItem label="Frozen Wallets" value={String(stats?.frozenWallets ?? 0)} trend="Restricted" color="var(--enterprise-warning)" />
        <StatItem label="Blocked Wallets" value={String(stats?.blockedWallets ?? 0)} trend="Suspended" color="var(--enterprise-danger)" />
        <StatItem label="Active Wallets" value={String(stats?.activeWallets ?? 0)} trend="Operational" color="var(--enterprise-success)" />
      </div>
    </div>
  );
}
