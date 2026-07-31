interface SimpleTableProps {
  headers: string[];
  rows: string[][];
}

export function SimpleTable({ headers, rows }: SimpleTableProps) {
  return (
    <div
      className="overflow-hidden rounded-md border border-[var(--enterprise-border)] bg-[var(--enterprise-card)]"
      style={{ overflow: 'hidden', borderRadius: '8px', border: '1px solid var(--enterprise-border)', background: 'var(--enterprise-card)' }}
    >
      <table
        className="min-w-full text-left text-sm"
        style={{ minWidth: '100%', textAlign: 'left', fontSize: '0.875rem', borderCollapse: 'collapse' }}
      >
        <thead
          className="bg-[var(--enterprise-card-hover)] text-[var(--enterprise-text-muted)]"
          style={{ background: 'var(--enterprise-card-hover)', color: 'var(--enterprise-text-muted)' }}
        >
          <tr>
            {headers.map((header) => (
              <th
                key={header}
                className="px-3 py-2 font-medium"
                style={{ padding: '0.75rem 1rem', fontWeight: '600', fontSize: '0.75rem', textTransform: 'uppercase', letterSpacing: '0.05em' }}
              >
                {header}
              </th>
            ))}
          </tr>
        </thead>
        <tbody
          className="divide-y divide-[var(--enterprise-border)] text-[var(--enterprise-text)]"
          style={{ color: 'var(--enterprise-text)' }}
        >
          {rows.length > 0 ? (
            rows.map((row, index) => (
              <tr
                key={index}
                className="bg-[var(--enterprise-background)] transition-colors hover:bg-[var(--enterprise-card-hover)]"
                style={{ background: 'var(--enterprise-background)', borderBottom: '1px solid var(--enterprise-border)', transition: 'background 200ms' }}
                onMouseEnter={(e) => {
                  (e.currentTarget as HTMLTableRowElement).style.background = 'var(--enterprise-card-hover)';
                }}
                onMouseLeave={(e) => {
                  (e.currentTarget as HTMLTableRowElement).style.background = 'var(--enterprise-background)';
                }}
              >
                {row.map((column, columnIndex) => (
                  <td
                    key={columnIndex}
                    className="px-3 py-2"
                    style={{ padding: '0.75rem 1rem' }}
                  >
                    {column}
                  </td>
                ))}
              </tr>
            ))
          ) : (
            <tr>
              <td
                colSpan={headers.length}
                className="px-3 py-8 text-center text-[var(--enterprise-text-muted)]"
                style={{ padding: '2rem 1rem', textAlign: 'center', color: 'var(--enterprise-text-muted)' }}
              >
                No records found.
              </td>
            </tr>
          )}
        </tbody>
      </table>
    </div>
  );
}
