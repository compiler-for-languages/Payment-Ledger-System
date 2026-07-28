import { FormEvent, useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useAuthStore } from "../store/authStore";

export function RegisterPage() {
  const navigate = useNavigate();
  const register = useAuthStore((state) => state.register);
  const isAuthenticated = useAuthStore((state) => state.isAuthenticated);

  const [fullName, setFullName] = useState("");
  const [username, setUsername] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const [registerSuccess, setRegisterSuccess] = useState(false);

  // Navigate only after isAuthenticated is confirmed to be true
  useEffect(() => {
    if (registerSuccess && isAuthenticated) {
      navigate("/user");
      setRegisterSuccess(false);
    }
  }, [registerSuccess, isAuthenticated, navigate]);

  const handleSubmit = async (event: FormEvent) => {
    event.preventDefault();
    setLoading(true);
    setError(null);
    try {
      await register({ fullName, username, email, password });
      // Set flag to trigger navigation in useEffect when state is updated
      setRegisterSuccess(true);
    } catch (err: any) {
      setError(err.response?.data?.message || "Registration failed");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="space-y-6">
      <header className="space-y-1 text-center">
        <p className="text-xs uppercase tracking-[0.2em] text-[var(--spotify-ink-muted)]">Onboarding</p>
        <h1 className="text-2xl font-semibold tracking-[-0.02em]">Register User</h1>
      </header>
      <form onSubmit={handleSubmit} className="space-y-4">
        <input
          placeholder="Full Name"
          value={fullName}
          onChange={(event) => setFullName(event.target.value)}
          className="spotify-input"
          required
        />
        <input
          placeholder="Username"
          value={username}
          onChange={(event) => setUsername(event.target.value)}
          className="spotify-input"
          required
        />
        <input
          type="email"
          placeholder="Email"
          value={email}
          onChange={(event) => setEmail(event.target.value)}
          className="spotify-input"
          required
        />
        <input
          type="password"
          placeholder="Password"
          value={password}
          onChange={(event) => setPassword(event.target.value)}
          className="spotify-input"
          required
          minLength={6}
        />
        {error ? <p className="text-sm text-red-400">{error}</p> : null}
        <button 
          type="submit"
          disabled={loading}
          className="spotify-btn-primary w-full"
        >
          {loading ? "Creating account..." : "Register"}
        </button>
      </form>
      <p className="text-center text-sm text-[var(--spotify-ink-muted)]">
        Already have an account?{" "}
        <Link to="/login" className="text-[var(--spotify-primary)] hover:text-[var(--spotify-primary-hover)]">
          Login
        </Link>
      </p>
    </div>
  );
}
