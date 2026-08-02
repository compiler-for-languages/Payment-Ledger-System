import { FormEvent, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useAuthStore } from "../store/authStore";

export function LoginPage() {
  const navigate = useNavigate();
  const login = useAuthStore((state) => state.login);

  const [email, setEmail] = useState("user@infotact.com");
  const [password, setPassword] = useState("password");
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (event: FormEvent) => {
    event.preventDefault();
    setError(null);
    setLoading(true);

    try {
      await login(email, password);
      navigate("/user");
    } catch (err: any) {
      setError(err.response?.data?.message || "Invalid credentials");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="space-y-6">
      <header className="space-y-1 text-center">
        <p className="text-xs uppercase tracking-[0.2em] text-[var(--spotify-ink-muted)]">Payment Ledger & Wallet</p>
        <h1 className="text-2xl font-semibold tracking-[-0.02em]">Secure Login</h1>
      </header>
      <form onSubmit={handleSubmit} className="space-y-4">
        <label className="block space-y-1">
          <span className="text-sm text-[var(--spotify-ink-muted)]">Email</span>
          <input
            type="email"
            value={email}
            onChange={(event) => setEmail(event.target.value)}
            className="spotify-input"
            required
          />
        </label>
        <label className="block space-y-1">
          <span className="text-sm text-[var(--spotify-ink-muted)]">Password</span>
          <input
            type="password"
            value={password}
            onChange={(event) => setPassword(event.target.value)}
            className="spotify-input"
            required
          />
        </label>
        {error ? <p className="text-sm text-red-400">{error}</p> : null}
        <button
          type="submit"
          disabled={loading}
          className="spotify-btn-primary w-full disabled:opacity-60"
        >
          {loading ? "Signing in..." : "Login"}
        </button>
      </form>
      <p className="text-center text-sm text-[var(--spotify-ink-muted)]">
        No account?{" "}
        <Link to="/register" className="text-[var(--spotify-primary)] hover:text-[var(--spotify-primary-hover)]">
          Create one
        </Link>
      </p>
    </div>
  );
}
