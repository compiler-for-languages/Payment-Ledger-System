export function LoadingScreen() {
  return (
    <div className="grid min-h-screen place-items-center bg-[var(--spotify-canvas)] text-[var(--spotify-ink)]">
      <div className="flex items-center gap-3 text-sm tracking-wide">
        <span className="h-2 w-2 animate-pulse rounded-full bg-[var(--spotify-primary)]" />
        Initializing Payment Ledger & Wallet System
      </div>
    </div>
  );
}
