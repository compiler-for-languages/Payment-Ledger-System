import { useEffect, useState } from "react";
import { PageHeader } from "../components/PageHeader";
import { SimpleTable } from "../components/SimpleTable";
import { adminService } from "../services/AdminService";
import { AuthUser } from "../types/auth";

export function UserManagementPage() {
  const [users, setUsers] = useState<AuthUser[]>([]);

  useEffect(() => {
    adminService.getAllUsers().then(setUsers).catch(console.error);
  }, []);

  return (
    <div className="space-y-6" style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
      <PageHeader title="User Management" description="Search, monitor, and administer user accounts." />
      <SimpleTable
        headers={["User ID", "Full Name", "Username", "Email", "Role"]}
        rows={users.map(u => [
          String(u.id),
          u.fullName,
          u.username,
          u.email,
          u.role
        ])}
      />
    </div>
  );
}
