import { FormEvent, useState } from "react";
import { PageHeader } from "../components/PageHeader";
import { userService } from "../services/UserService";

export function SettingsPage() {
  const [oldPassword, setOldPassword] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const [message, setMessage] = useState("");
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (event: FormEvent) => {
    event.preventDefault();
    setLoading(true);
    setMessage("");
    try {
      await userService.changePassword({ oldPassword, newPassword });
      setMessage("Password updated successfully.");
      setOldPassword("");
      setNewPassword("");
    } catch (err: any) {
      setMessage(err.response?.data?.message || "Failed to update password.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="space-y-6" style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
      <PageHeader title="Settings" description="Security and session preferences for your account." />
      <form onSubmit={handleSubmit} className="max-w-md space-y-3" style={{ maxWidth: '28rem', display: 'flex', flexDirection: 'column', gap: '1rem' }}>
        <div>
          <label style={{ display: 'block', fontSize: '0.875rem', color: 'var(--enterprise-text-muted)', marginBottom: '0.5rem' }}>
            Old Password
          </label>
          <input
            type="password"
            placeholder="Enter old password"
            value={oldPassword}
            onChange={(event) => setOldPassword(event.target.value)}
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
            required
          />
        </div>
        <div>
          <label style={{ display: 'block', fontSize: '0.875rem', color: 'var(--enterprise-text-muted)', marginBottom: '0.5rem' }}>
            New Password
          </label>
          <input
            type="password"
            placeholder="Enter new password (min 6 characters)"
            value={newPassword}
            onChange={(event) => setNewPassword(event.target.value)}
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
            required
            minLength={6}
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
          {loading ? "Updating..." : "Change Password"}
        </button>
      </form>
      {message ? (
        <p style={{
          fontSize: '0.875rem',
          color: message.includes("Failed") ? 'var(--enterprise-danger)' : 'var(--enterprise-success)',
          padding: '1rem',
          borderRadius: '8px',
          background: message.includes("Failed") ? 'rgba(239, 68, 68, 0.1)' : 'rgba(34, 197, 94, 0.1)',
          border: `1px solid ${message.includes("Failed") ? 'var(--enterprise-danger)' : 'var(--enterprise-success)'}`
        }}>
          {message}
        </p>
      ) : null}
    </div>
  );
}
