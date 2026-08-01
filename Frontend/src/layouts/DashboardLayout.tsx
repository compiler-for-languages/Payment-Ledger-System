import { Outlet } from "react-router-dom";
import { Sidebar } from "../components/Sidebar";
import { Topbar } from "../components/Topbar";
import { SystemFooter } from "../common/SystemFooter";

export function DashboardLayout() {
  return (
    <div className="dashboard-shell">
      <Sidebar />
      <div className="dashboard-body">
        <Topbar />
        <main className="dashboard-content">
          <Outlet />
        </main>
      </div>
      <SystemFooter />
    </div>
  );
}
