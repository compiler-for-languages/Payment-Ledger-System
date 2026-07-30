import { useEffect, useMemo, useState } from "react";

const tracks = [
  { title: "Ledger Pulse", artist: "Infotact Session", color: "#4d3d8b" },
  { title: "Transaction Stream", artist: "Backend Rhythm", color: "#7f2f4e" },
  { title: "Audit Trail", artist: "Distributed Locks", color: "#224b63" },
];

export function NowPlayingBar() {
  const [trackIndex, setTrackIndex] = useState(0);
  const [progress, setProgress] = useState(12);

  useEffect(() => {
    const progressTimer = setInterval(() => {
      setProgress((current) => (current >= 100 ? 0 : current + 1));
    }, 900);

    const trackTimer = setInterval(() => {
      setTrackIndex((current) => (current + 1) % tracks.length);
      setProgress(0);
    }, 16000);

    return () => {
      clearInterval(progressTimer);
      clearInterval(trackTimer);
    };
  }, []);

  const activeTrack = tracks[trackIndex];

  const gradient = useMemo(
    () =>
      `linear-gradient(180deg, color-mix(in srgb, ${activeTrack.color} 60%, #121212) 0%, rgba(18,18,18,0.96) 75%)`,
    [activeTrack.color],
  );

  return (
    <footer className="fixed inset-x-0 bottom-0 z-20 h-[90px] border-t border-[var(--spotify-border)] bg-[var(--spotify-surface-1)]/95 px-4 backdrop-blur">
      <div className="grid h-full grid-cols-[1fr_1.5fr_1fr] items-center gap-4">
        <div className="flex items-center gap-3">
          <div
            className="h-12 w-12 rounded-md border border-black/20 transition-colors duration-[600ms]"
            style={{ background: gradient }}
            aria-hidden
          />
          <div className="min-w-0">
            <p className="truncate text-sm font-semibold text-[var(--spotify-ink)]">{activeTrack.title}</p>
            <p className="truncate text-xs text-[var(--spotify-ink-muted)]">{activeTrack.artist}</p>
          </div>
        </div>

        <div className="space-y-2">
          <div className="mx-auto flex max-w-xs items-center justify-center gap-4 text-sm text-[var(--spotify-ink-muted)]">
            <button className="transition hover:text-white">Prev</button>
            <button className="spotify-btn-primary h-10 w-10 p-0 text-xs">Play</button>
            <button className="transition hover:text-white">Next</button>
          </div>
          <div className="mx-auto h-1 max-w-md rounded-full bg-[var(--spotify-surface-3)]">
            <div
              className="h-1 rounded-full bg-[var(--spotify-primary)] transition-all duration-200"
              style={{ width: `${progress}%` }}
            />
          </div>
        </div>

        <div className="hidden justify-end text-xs text-[var(--spotify-ink-muted)] sm:flex">Queue | Volume</div>
      </div>
    </footer>
  );
}
