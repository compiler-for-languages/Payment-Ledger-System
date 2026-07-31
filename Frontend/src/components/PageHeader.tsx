interface PageHeaderProps {
  title: string;
  description: string;
}

export function PageHeader({ title, description }: PageHeaderProps) {
  return (
    <header
      className="space-y-1 border-b border-[var(--enterprise-border)] pb-4"
      style={{ marginBottom: '1.5rem', paddingBottom: '1rem', borderBottom: '1px solid var(--enterprise-border)' }}
    >
      <h1
        className="text-2xl font-bold tracking-[-0.03em] text-[var(--enterprise-text)]"
        style={{ fontSize: '1.75rem', fontWeight: '700', color: 'var(--enterprise-text)', marginBottom: '0.5rem', letterSpacing: '-0.02em' }}
      >
        {title}
      </h1>
      <p
        className="text-sm text-[var(--enterprise-text-muted)]"
        style={{ fontSize: '0.875rem', color: 'var(--enterprise-text-muted)', lineHeight: '1.5' }}
      >
        {description}
      </p>
    </header>
  );
}
