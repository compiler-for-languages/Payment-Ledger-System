interface ContentBlockProps {
  title: string;
  children: React.ReactNode;
}

export function ContentBlock({ title, children }: ContentBlockProps) {
  return (
    <section className="space-y-3 rounded-md border border-zinc-800 bg-zinc-950 p-4">
      <h2 className="text-sm font-semibold text-zinc-200">{title}</h2>
      {children}
    </section>
  );
}
