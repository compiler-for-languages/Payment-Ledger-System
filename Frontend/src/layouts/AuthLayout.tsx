import { Outlet } from "react-router-dom";

export function AuthLayout() {
  return (
    <div className="auth-shell">
      {/* Decorative gradient blobs */}
      <div
        style={{
          position: "absolute",
          inset: 0,
          background:
            "radial-gradient(circle at 15% 20%, rgba(29,185,84,0.26), transparent 34%), radial-gradient(circle at 82% 12%, rgba(255,255,255,0.08), transparent 30%)",
          pointerEvents: "none",
          zIndex: 0,
        }}
      />
      <div className="auth-card" style={{ position: "relative", zIndex: 1 }}>
        <Outlet />
      </div>
    </div>
  );
}
