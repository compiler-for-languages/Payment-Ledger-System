import { NavLink } from "react-router-dom";
import { useAuthStore } from "../store/authStore";

const userLinks = [
  { label: "Dashboard", to: "/user", icon: "📊" },
  { label: "Wallet", to: "/wallet", icon: "💳" },
  { label: "Deposit", to: "/deposit", icon: "⬇️" },
  { label: "Withdraw", to: "/withdraw", icon: "⬆️" },
  { label: "Transfer", to: "/transfer", icon: "↔️" },
  { label: "Transactions", to: "/transactions", icon: "📋" },
  { label: "Ledger", to: "/ledger", icon: "📒" },
  { label: "Profile", to: "/profile", icon: "👤" },
  { label: "Settings", to: "/settings", icon: "⚙️" },
];

const adminLinks = [
  { label: "Admin Dashboard", to: "/admin", icon: "🎯" },
  { label: "User Management", to: "/admin/users", icon: "👥" },
  { label: "Wallet Management", to: "/admin/wallets", icon: "💼" },
  { label: "Reports", to: "/admin/reports", icon: "📈" },
];

export function Sidebar() {
  const user = useAuthStore((state) => state.user);
  const links = user?.role === "ADMIN" ? [...adminLinks, ...userLinks] : userLinks;

  return (
    <aside className="dashboard-sidebar">
      <div
        style={{
          marginBottom: "1.5rem",
          paddingBottom: "1rem",
          borderBottom: "1px solid var(--enterprise-border)",
        }}
      >
        <p
          style={{
            fontSize: "0.7rem",
            textTransform: "uppercase",
            letterSpacing: "0.18em",
            color: "var(--enterprise-text-muted)",
          }}
        >
          Digital Payment
        </p>
        <p
          style={{
            fontSize: "0.875rem",
            fontWeight: 600,
            color: "var(--enterprise-text)",
            marginTop: "2px",
          }}
        >
          Transaction Engine
        </p>
      </div>
      <nav style={{ display: "flex", flexDirection: "column", gap: "2px" }}>
        {links.map((link) => (
          <NavLink
            key={link.to}
            to={link.to}
            style={({ isActive }) => ({
              display: "flex",
              alignItems: "center",
              gap: "0.75rem",
              borderRadius: "6px",
              padding: "10px 12px",
              fontSize: "0.875rem",
              textDecoration: "none",
              transition: "background 200ms, color 200ms",
              background: isActive ? "var(--enterprise-card-hover)" : "transparent",
              color: isActive ? "var(--enterprise-primary)" : "var(--enterprise-text-muted)",
            })}
            onMouseEnter={(e) => {
              const el = e.currentTarget as HTMLAnchorElement;
              if (!el.classList.contains("active")) {
                el.style.background = "var(--enterprise-card-hover)";
                el.style.color = "var(--enterprise-text)";
              }
            }}
            onMouseLeave={(e) => {
              const el = e.currentTarget as HTMLAnchorElement;
              el.style.background = "";
              el.style.color = "";
            }}
          >
            <span style={{ fontSize: "1rem" }}>{link.icon}</span>
            {link.label}
          </NavLink>
        ))}
      </nav>
    </aside>
  );
}
