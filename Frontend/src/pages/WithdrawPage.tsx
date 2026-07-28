import { FormEvent, useState } from "react";
import { PageHeader } from "../components/PageHeader";
import { transactionService } from "../services/TransactionService";

export function WithdrawPage() {
  const [amount, setAmount] = useState(0);
  const [description, setDescription] = useState("");
  const [message, setMessage] = useState("");
  const [loading, setLoading] = useState(false);

  const submit = async (event: FormEvent) => {
    event.preventDefault();
    setLoading(true);
    try {
      const response = await transactionService.withdraw(amount, description || "Withdraw");
      setMessage(`Withdraw successful. Reference: ${response.referenceNumber}`);
      setAmount(0);
      setDescription("");
    } catch (err: any) {
      setMessage(err.response?.data?.message || "Withdraw failed");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="space-y-6" style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
      <PageHeader title="Withdraw" description="Debit workflow with service-side balance and state validations." />
      <form onSubmit={submit} className="max-w-md space-y-3" style={{ maxWidth: '28rem', display: 'flex', flexDirection: 'column', gap: '1rem' }}>
        <div>
          <label style={{ display: 'block', fontSize: '0.875rem', color: 'var(--enterprise-text-muted)', marginBottom: '0.5rem' }}>
            Amount
          </label>
          <input
            type="number"
            value={amount}
            onChange={(event) => setAmount(Number(event.target.value))}
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
            placeholder="Enter amount"
            min="1"
            required
          />
        </div>
        <div>
          <label style={{ display: 'block', fontSize: '0.875rem', color: 'var(--enterprise-text-muted)', marginBottom: '0.5rem' }}>
            Description
          </label>
          <input
            value={description}
            onChange={(event) => setDescription(event.target.value)}
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
            placeholder="Enter description"
          />
        </div>
        <button
          disabled={loading}
          style={{
            borderRadius: '6px',
            border: 'none',
            padding: '12px 24px',
            background: loading ? 'var(--enterprise-border)' : 'var(--enterprise-primary)',
            color: '#000',
            fontWeight: '600',
            fontSize: '0.875rem',
            cursor: loading ? 'not-allowed' : 'pointer',
            transition: 'background 200ms'
          }}
        >
          {loading ? "Processing..." : "Submit Withdraw"}
        </button>
      </form>
      {message ? (
        <p style={{
          fontSize: '0.875rem',
          color: message.includes("failed") ? 'var(--enterprise-danger)' : 'var(--enterprise-success)',
          padding: '1rem',
          borderRadius: '8px',
          background: message.includes("failed") ? 'rgba(239, 68, 68, 0.1)' : 'rgba(34, 197, 94, 0.1)',
          border: `1px solid ${message.includes("failed") ? 'var(--enterprise-danger)' : 'var(--enterprise-success)'}`
        }}>
          {message}
        </p>
      ) : null}
    </div>
  );
}
