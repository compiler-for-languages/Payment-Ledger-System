import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuthStore } from "../store/authStore";

export function Topbar() {
  const navigate = useNavigate();
  const user = useAuthStore((state) => state.user);
  const logout = useAuthStore((state) => state.logout);
  const isAuthenticated = useAuthStore((state) => state.isAuthenticated);
  const [shouldLogout, setShouldLogout] = useState(false);

  // Navigate only after logout is confirmed (isAuthenticated is false)
  useEffect(() => {
    if (shouldLogout && !isAuthenticated) {
      navigate("/login");
      setShouldLogout(false);
    }
  }, [shouldLogout, isAuthenticated, navigate]);

  const handleLogout = () => {
    logout();
    setShouldLogout(true);
  };

  return (
    <header className="dashboard-topbar">
      <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
        <div
          style={{
            width: '40px',
            height: '40px',
            borderRadius: '8px',
            background: 'linear-gradient(135deg, var(--enterprise-primary) 0%, var(--enterprise-primary-hover) 100%)',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            fontSize: '1.25rem',
            fontWeight: 'bold',
            color: '#000'
          }}
        >
          SSS
        </div>
        <div>
          <p style={{ fontSize: "0.7rem", textTransform: "uppercase", letterSpacing: "0.16em", color: "var(--enterprise-text-muted)" }}>
            LOSM Payment Ledger
          </p>
          <p style={{ fontSize: "0.875rem", color: "var(--enterprise-text)", fontWeight: 500 }}>
            Digital Payment System
          </p>
        </div>
      </div>

      <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
          <span
            style={{
              padding: '4px 12px',
              borderRadius: '9999px',
              fontSize: '0.75rem',
              fontWeight: 600,
              background: user?.role === 'ADMIN' ? 'var(--enterprise-warning)' : 'var(--enterprise-info)',
              color: '#000'
            }}
          >
            {user?.role || 'USER'}
          </span>
          <span style={{ fontSize: '0.875rem', color: 'var(--enterprise-text)' }}>
            {user?.fullName || 'Guest'}
          </span>
        </div>

        <button
          onClick={handleLogout}
          style={{
            borderRadius: "6px",
            border: "1px solid var(--enterprise-border)",
            padding: "8px 16px",
            fontSize: "0.875rem",
            color: "var(--enterprise-text)",
            background: "transparent",
            cursor: "pointer",
            transition: "background 200ms, border-color 200ms",
          }}
          onMouseEnter={(e) => {
            (e.currentTarget as HTMLButtonElement).style.background = "var(--enterprise-card-hover)";
          }}
          onMouseLeave={(e) => {
            (e.currentTarget as HTMLButtonElement).style.background = "transparent";
          }}
        >
          Logout
        </button>
      </div>
    </header>
  );
}
