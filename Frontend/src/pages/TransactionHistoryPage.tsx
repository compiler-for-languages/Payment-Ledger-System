import { useEffect, useMemo, useState } from "react";
import { PageHeader } from "../components/PageHeader";
import { SimpleTable } from "../components/SimpleTable";
import { useDebounce } from "../hooks/useDebounce";
import { useTransactionStore } from "../store/transactionStore";
import { formatCurrency, formatDateTime } from "../utils/format";

export function TransactionHistoryPage() {
  const [search, setSearch] = useState("");
  const debouncedSearch = useDebounce(search);
  const transactions = useTransactionStore((state) => state.transactions);

  useEffect(() => {
    // Call via getState() so the action reference is never a reactive dependency.
    useTransactionStore.getState().loadTransactions();
  }, []);

  const filtered = useMemo(
    () =>
      transactions.filter((txn) =>
        `${txn.referenceNumber}${txn.type}${txn.status}`.toLowerCase().includes(debouncedSearch.toLowerCase()),
      ),
    [debouncedSearch, transactions],
  );

  return (
    <div className="space-y-6" style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
      <PageHeader title="Transaction History" description="Search and filter your own financial operations." />
      <input
        value={search}
        onChange={(event) => setSearch(event.target.value)}
        placeholder="Search by reference, type or status"
        style={{
          width: '100%',
          borderRadius: '8px',
          border: '1px solid var(--enterprise-border)',
          background: 'var(--enterprise-card)',
          color: 'var(--enterprise-text)',
          padding: '12px 16px',
          fontSize: '0.875rem',
          transition: 'border-color 200ms'
        }}
      />
      <SimpleTable
        headers={["Reference", "Type", "Status", "Amount", "Timestamp"]}
        rows={filtered.map((txn) => [
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
