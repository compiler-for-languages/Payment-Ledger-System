import { Link } from "react-router-dom";

export function NotFoundPage() {
  return (
    <main className="grid min-h-screen place-items-center bg-zinc-950 px-6 text-center text-zinc-100">
      <div className="space-y-3">
        <p className="text-sm uppercase tracking-[0.2em] text-zinc-500">Error 404</p>
        <h1 className="text-3xl font-semibold">Page Not Found</h1>
        <p className="text-zinc-400">The route you requested does not exist in this workspace.</p>
        <Link to="/login" className="inline-block rounded-md border border-zinc-700 px-4 py-2 text-sm hover:bg-zinc-900">
          Go to Login
        </Link>
      </div>
    </main>
  );
}
