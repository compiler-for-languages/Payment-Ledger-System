import { useAuthStore } from "../store/authStore";

export function SystemFooter() {
  const user = useAuthStore((state) => state.user);

  return (
    <footer
      style={{
        position: 'fixed',
        bottom: 0,
        left: 0,
        right: 0,
        zIndex: 20,
        height: '60px',
        borderTop: '1px solid var(--enterprise-border)',
        background: 'rgba(23, 26, 33, 0.95)',
        padding: '0 1rem',
        backdropFilter: 'blur(12px)',
        WebkitBackdropFilter: 'blur(12px)',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'space-between',
        fontSize: '0.75rem',
        color: 'var(--enterprise-text-muted)'
      }}
    >
      <div style={{ display: 'flex', alignItems: 'center', gap: '1.5rem' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
          <span
            style={{
              width: '8px',
              height: '8px',
              borderRadius: '50%',
              background: '#22c55e',
              display: 'inline-block'
            }}
          />
          <span>System Status: <strong style={{ color: '#22c55e' }}>Online</strong></span>
        </div>
        <div>Environment: <strong>Development</strong></div>
        <div>Version: <strong>1.0.0</strong></div>
      </div>

      <div style={{ display: 'flex', alignItems: 'center', gap: '1.5rem' }}>
        <div>API: <strong style={{ color: '#22c55e' }}>Healthy</strong></div>
        <div>Database: <strong style={{ color: '#22c55e' }}>Connected</strong></div>
        <div>Redis: <strong style={{ color: '#22c55e' }}>Active</strong></div>
        <div style={{ borderLeft: '1px solid var(--enterprise-border)', paddingLeft: '1rem' }}>
          User: <strong>{user?.fullName || 'N/A'}</strong>
        </div>
      </div>
    </footer>
  );
}
