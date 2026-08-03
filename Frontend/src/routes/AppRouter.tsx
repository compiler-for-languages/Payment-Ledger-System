import { useEffect } from "react";
import { BrowserRouter, Navigate, Route, Routes } from "react-router-dom";
import { LoadingScreen } from "../common/LoadingScreen";
import { ProtectedRoute } from "../components/ProtectedRoute";
import { DashboardLayout } from "../layouts/DashboardLayout";
import { AuthLayout } from "../layouts/AuthLayout";
import { useAuthStore } from "../store/authStore";

// Pages
import { AdminDashboardPage } from "../pages/AdminDashboardPage";
import { DepositPage } from "../pages/DepositPage";
import { LedgerHistoryPage } from "../pages/LedgerHistoryPage";
import { LoginPage } from "../pages/LoginPage";
import { NotFoundPage } from "../pages/NotFoundPage";
import { ProfilePage } from "../pages/ProfilePage";
import { RegisterPage } from "../pages/RegisterPage";
import { ReportsPage } from "../pages/ReportsPage";
import { SettingsPage } from "../pages/SettingsPage";
import { TransactionHistoryPage } from "../pages/TransactionHistoryPage";
import { TransferPage } from "../pages/TransferPage";
import { UserDashboardPage } from "../pages/UserDashboardPage";
import { UserManagementPage } from "../pages/UserManagementPage";
import { WalletManagementPage } from "../pages/WalletManagementPage";
import { WalletPage } from "../pages/WalletPage";
import { WithdrawPage } from "../pages/WithdrawPage";

export function AppRouter() {
  const isHydrated = useAuthStore((state) => state.isHydrated);

  useEffect(() => {
    // Call via getState() so the function reference is never a dependency.
    // The module-level _initializeCalled flag inside authStore ensures this
    // runs exactly once, even under React StrictMode's double-invocation.
    useAuthStore.getState().initialize();
  }, []);

  if (!isHydrated) {
    return <LoadingScreen />;
  }

  return (
    <BrowserRouter>
      <Routes>
        {/* Auth Routes */}
        <Route element={<AuthLayout />}>
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />
        </Route>

        {/* Dashboard Routes - adding back DashboardLayout for testing */}
        <Route element={<DashboardLayout />}>
          <Route path="/user" element={<UserDashboardPage />} />
          <Route path="/wallet" element={<WalletPage />} />
          <Route path="/deposit" element={<DepositPage />} />
          <Route path="/withdraw" element={<WithdrawPage />} />
          <Route path="/transfer" element={<TransferPage />} />
          <Route path="/transactions" element={<TransactionHistoryPage />} />
          <Route path="/ledger" element={<LedgerHistoryPage />} />
          <Route path="/profile" element={<ProfilePage />} />
          <Route path="/settings" element={<SettingsPage />} />

          {/* Admin Routes */}
          <Route path="/admin" element={<AdminDashboardPage />} />
          <Route path="/admin/users" element={<UserManagementPage />} />
          <Route path="/admin/wallets" element={<WalletManagementPage />} />
          <Route path="/admin/reports" element={<ReportsPage />} />
        </Route>

        {/* Fallbacks */}
        <Route path="/" element={<Navigate to="/login" replace />} />
        <Route path="*" element={<NotFoundPage />} />
      </Routes>
    </BrowserRouter>
  );
}
