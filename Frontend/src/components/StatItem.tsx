interface StatItemProps {
  label: string;
  value: string;
  trend?: string;
  color?: string;
}

export function StatItem({ label, value, trend, color }: StatItemProps) {
  const defaultColor = color || 'var(--enterprise-primary)';

  return (
    <div
      className="surface-panel p-4 transition-transform duration-200 hover:translate-y-[-2px]"
      style={{
        border: '1px solid var(--enterprise-border)',
        borderRadius: '8px',
        background: 'var(--enterprise-card)',
        padding: '1.25rem',
        transition: 'transform 200ms, box-shadow 200ms'
      }}
      onMouseEnter={(e) => {
        (e.currentTarget as HTMLDivElement).style.transform = 'translateY(-2px)';
        (e.currentTarget as HTMLDivElement).style.boxShadow = '0 8px 24px rgba(0, 0, 0, 0.3)';
      }}
      onMouseLeave={(e) => {
        (e.currentTarget as HTMLDivElement).style.transform = 'translateY(0)';
        (e.currentTarget as HTMLDivElement).style.boxShadow = 'none';
      }}
    >
      <p
        className="text-xs uppercase tracking-[0.12em] text-[var(--enterprise-text-muted)]"
        style={{ fontSize: '0.75rem', textTransform: 'uppercase', letterSpacing: '0.12em', color: 'var(--enterprise-text-muted)' }}
      >
        {label}
      </p>
      <p
        className="mt-2 text-lg font-semibold text-[var(--enterprise-text)]"
        style={{ marginTop: '0.75rem', fontSize: '1.5rem', fontWeight: '600', color: 'var(--enterprise-text)' }}
      >
        {value}
      </p>
      {trend && (
        <p
          style={{ marginTop: '0.5rem', fontSize: '0.75rem', color: defaultColor }}
        >
          {trend}
        </p>
      )}
    </div>
  );
}
