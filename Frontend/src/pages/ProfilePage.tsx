import { PageHeader } from "../components/PageHeader";
import { useAuthStore } from "../store/authStore";

export function ProfilePage() {
  const user = useAuthStore((state) => state.user);

  return (
    <div className="space-y-6" style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
      <PageHeader title="Profile" description="View your authenticated profile information." />
      <dl style={{ display: 'grid', gap: '1rem', fontSize: '0.875rem', color: 'var(--enterprise-text)', gridTemplateColumns: 'repeat(auto-fit, minmax(280px, 1fr))' }}>
        <div style={{ borderRadius: '8px', border: '1px solid var(--enterprise-border)', background: 'var(--enterprise-card)', padding: '1.25rem' }}>
          <dt style={{ color: 'var(--enterprise-text-muted)', fontSize: '0.75rem', textTransform: 'uppercase', letterSpacing: '0.05em', marginBottom: '0.5rem' }}>Full Name</dt>
          <dd style={{ fontSize: '1rem', fontWeight: '500' }}>{user?.fullName || '-'}</dd>
        </div>
        <div style={{ borderRadius: '8px', border: '1px solid var(--enterprise-border)', background: 'var(--enterprise-card)', padding: '1.25rem' }}>
          <dt style={{ color: 'var(--enterprise-text-muted)', fontSize: '0.75rem', textTransform: 'uppercase', letterSpacing: '0.05em', marginBottom: '0.5rem' }}>Username</dt>
          <dd style={{ fontSize: '1rem', fontWeight: '500' }}>{user?.username || '-'}</dd>
        </div>
        <div style={{ borderRadius: '8px', border: '1px solid var(--enterprise-border)', background: 'var(--enterprise-card)', padding: '1.25rem' }}>
          <dt style={{ color: 'var(--enterprise-text-muted)', fontSize: '0.75rem', textTransform: 'uppercase', letterSpacing: '0.05em', marginBottom: '0.5rem' }}>Email</dt>
          <dd style={{ fontSize: '1rem', fontWeight: '500' }}>{user?.email || '-'}</dd>
        </div>
        <div style={{ borderRadius: '8px', border: '1px solid var(--enterprise-border)', background: 'var(--enterprise-card)', padding: '1.25rem' }}>
          <dt style={{ color: 'var(--enterprise-text-muted)', fontSize: '0.75rem', textTransform: 'uppercase', letterSpacing: '0.05em', marginBottom: '0.5rem' }}>Role</dt>
          <dd style={{ fontSize: '1rem', fontWeight: '500' }}>
            <span style={{
              padding: '4px 12px',
              borderRadius: '9999px',
              fontSize: '0.75rem',
              fontWeight: '600',
              background: user?.role === 'ADMIN' ? 'var(--enterprise-warning)' : 'var(--enterprise-info)',
              color: '#000'
            }}>
              {user?.role || 'USER'}
            </span>
          </dd>
        </div>
      </dl>
    </div>
  );
}
