import { useEffect, useState } from "react";
import { PageHeader } from "../components/PageHeader";
import { StatItem } from "../components/StatItem";
import { adminService } from "../services/AdminService";
import { TransactionSummary } from "../types/transaction";
import { formatCurrency } from "../utils/format";

export function ReportsPage() {
  const [summary, setSummary] = useState<TransactionSummary | null>(null);

  useEffect(() => {
    adminService.getTransactionSummary()
      .then(setSummary)
      .catch(console.error);
  }, []);

  const successRatio = summary ? ((summary.successfulTransactions / summary.totalTransactions) * 100).toFixed(1) : "0";

  return (
    <div className="space-y-6" style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
      <PageHeader title="Reports" description="Summary reporting for operations, failure rate, and compliance insights." />
      <div className="grid gap-4 sm:grid-cols-3" style={{ display: 'grid', gap: '1rem', gridTemplateColumns: 'repeat(auto-fit, minmax(280px, 1fr))' }}>
        <StatItem label="Total Transactions" value={String(summary?.totalTransactions ?? 0)} trend="All Operations" color="var(--enterprise-info)" />
        <StatItem label="Success Ratio" value={`${successRatio}%`} trend="Success Rate" color={parseFloat(successRatio) > 90 ? 'var(--enterprise-success)' : 'var(--enterprise-warning)'} />
        <StatItem label="Failed Transactions" value={String(summary?.failedTransactions ?? 0)} trend="Error Count" color="var(--enterprise-danger)" />

        <StatItem label="Total Deposits" value={formatCurrency(summary?.totalDepositAmount ?? 0)} trend="Inbound Flow" color="var(--enterprise-success)" />
        <StatItem label="Total Withdrawals" value={formatCurrency(summary?.totalWithdrawalAmount ?? 0)} trend="Outbound Flow" color="var(--enterprise-warning)" />
        <StatItem label="Total Transfers" value={formatCurrency(summary?.totalTransferAmount ?? 0)} trend="Internal Flow" color="var(--enterprise-primary)" />
      </div>
    </div>
  );
}
