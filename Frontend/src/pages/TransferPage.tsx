import { FormEvent, useState } from "react";
import { PageHeader } from "../components/PageHeader";
import { transactionService } from "../services/TransactionService";

export function TransferPage() {
  const [receiverId, setReceiverId] = useState("");
  const [description, setDescription] = useState("");
  const [amount, setAmount] = useState("");
  const [message, setMessage] = useState("");
  const [messageType, setMessageType] = useState<"success" | "error" | "">("");
  const [loading, setLoading] = useState(false);

  const submit = async (event: FormEvent) => {
    event.preventDefault();
    setLoading(true);
    try {
      const response = await transactionService.transfer({
        receiverUserId: Number(receiverId),
        description,
        amount: Number(amount),
        idempotencyKey: crypto.randomUUID(),
      });
      if (response.status === "FAILED") {
        setMessage("Transfer failed. Insufficient wallet balance.");
        setMessageType("error");
      } else {
        setMessage(`Transfer successful. Reference: ${response.referenceNumber}`);
        setMessageType("success");
        setReceiverId("");
        setDescription("");
        setAmount("");
      }
    } catch (err: any) {
      setMessage(err.response?.data?.message || "Transfer failed");
      setMessageType("error");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="space-y-6" style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
      <PageHeader title="Transfer" description="Transfer flow with idempotency key generation and response tracking." />
      <form onSubmit={submit} className="max-w-xl space-y-3" style={{ maxWidth: '32rem', display: 'flex', flexDirection: 'column', gap: '1rem' }}>
        <div>
          <label style={{ display: 'block', fontSize: '0.875rem', color: 'var(--enterprise-text-muted)', marginBottom: '0.5rem' }}>
            Receiver User ID
          </label>
          <input
            value={receiverId}
            onChange={(event) => setReceiverId(event.target.value)}
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
            placeholder="Enter receiver user ID"
            required
          />
        </div>
        <div>
          <label style={{ display: 'block', fontSize: '0.875rem', color: 'var(--enterprise-text-muted)', marginBottom: '0.5rem' }}>
            Amount
          </label>
          <input
            type="number"
            value={amount}
            onChange={(event) => setAmount(event.target.value)}
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
          <textarea
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
              transition: 'border-color 200ms',
              minHeight: '80px',
              resize: 'vertical'
            }}
            placeholder="Enter description"
            rows={3}
            required
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
          {loading ? "Processing..." : "Submit Transfer"}
        </button>
      </form>
      {message ? (
        <p style={{
          fontSize: '0.875rem',
          color: messageType === "error" ? 'var(--enterprise-danger)' : 'var(--enterprise-success)',
          padding: '1rem',
          borderRadius: '8px',
          background: messageType === "error" ? 'rgba(239, 68, 68, 0.1)' : 'rgba(34, 197, 94, 0.1)',
          border: `1px solid ${messageType === "error" ? 'var(--enterprise-danger)' : 'var(--enterprise-success)'}`
        }}>
          {message}
        </p>
      ) : null}
    </div>
  );
}
