import React, { useState } from "react";
import { BrowserRouter as Router, Routes, Route, Navigate } from "react-router-dom";

/* ================= LAYOUTS ================= */
// These components provide the Sidebar and Topbar for each role
import EmployeeLayout from "./components/EmployeeLayout";
import AdminLayout from "./components/AdminLayout"; 
import AccountantLayout from "./components/AccountantLayout"; 

/* ================= COMMON ================= */
import Landing from "./pages/Landing";

/* ================= LOGIN ================= */
import AdminLogin from "./pages/Login/AdminLogin";
import AccountantLogin from "./pages/Login/AccountantLogin";
import EmployeeLogin from "./pages/Login/EmployeeLogin";

/* ================= ADMIN PAGES ================= */
import AdminDashboard from "./pages/Admin/AdminDashboard";
import Employees from "./pages/Admin/Employees";
import Attendance from "./pages/Admin/Attendance";
import Leave from "./pages/Admin/Leave";
import AdminPayroll from "./pages/Admin/Payroll"; // Renamed import to avoid conflict
import Report from "./pages/Admin/Report";
import SystemConfig from "./pages/Admin/System-Config";
import Forgotpw from "./pages/Admin/Forgotpw";

/* ================= ACCOUNTANT PAGES ================= */
import AccountantDashboard from "./pages/Accountant/AccountantDashboard"; 
import AccountantPayroll from "./pages/Accountant/Payroll"; // New Functional Component
import Forgotpass from "./pages/Accountant/Forgotpass";

/* ================= EMPLOYEE PAGES ================= */
import EmployeeDashboard from "./pages/Employee/EmployeeDashboard";
import AttendanceRecords from "./pages/Employee/AttendanceRecords";
import LeaveManagement from "./pages/Employee/LeaveManagement";
import SalaryAnalytics from "./pages/Employee/SalaryAnalytics";
import Settings from "./pages/Employee/Settings";
import ForgotPassword from "./pages/Employee/ForgotPassword";

/* ================= AUTH GUARD ================= */
// Prevents unauthorized access (e.g., Employee accessing Admin panel)
const ProtectedRoute = ({ user, allowedRole, children }) => {
  if (!user) {
    return <Navigate to="/" replace />;
  }
  if (user.role !== allowedRole) {
    return <Navigate to="/" replace />;
  }
  return children;
};

function App() {
  // Session management logic
  const [user, setUser] = useState(() => {
    const savedUser = localStorage.getItem("user_session");
    return savedUser ? JSON.parse(savedUser) : null;
  });

  return (
    <Router>
      <Routes>
        {/* Public Landing Page */}
        <Route path="/" element={<Landing />} />

        {/* ================= LOGIN ROUTES ================= */}
        <Route path="/login/admin" element={<AdminLogin setUser={setUser} />} />
        <Route path="/login/accountant" element={<AccountantLogin setUser={setUser} />} />
        <Route path="/login/employee" element={<EmployeeLogin setUser={setUser} />} />

        {/* ================= ADMIN PANEL (PROTECTED) ================= */}
        <Route 
          path="/admin" 
          element={
            <ProtectedRoute user={user} allowedRole="admin">
              <AdminLayout />
            </ProtectedRoute>
          }
        >
          <Route index element={<Navigate to="dashboard" replace />} /> 
          <Route path="dashboard" element={<AdminDashboard />} />
          <Route path="employees" element={<Employees />} />
          <Route path="attendance" element={<Attendance />} />
          <Route path="leave" element={<Leave />} />
          <Route path="payroll" element={<AdminPayroll />} />
          <Route path="report" element={<Report />} />
          <Route path="system-config" element={<SystemConfig />} />
        </Route>
        <Route path="/admin/forgot-password" element={<Forgotpw />} />

        {/* ================= ACCOUNTANT PANEL (PROTECTED) ================= */}
        <Route 
          path="/accountant" 
          element={
            <ProtectedRoute user={user} allowedRole="accountant">
              <AccountantLayout />
            </ProtectedRoute>
          } 
        >
          <Route index element={<Navigate to="dashboard" replace />} />
          <Route path="dashboard" element={<AccountantDashboard />} />
          {/* Matches the 'to="payroll"' in your AccountantLayout.jsx menuItems */}
          <Route path="payroll" element={<AccountantPayroll />} />
          <Route path="tax-verification" element={<div>Tax Verification Logic Coming Soon</div>} />
          <Route path="reports" element={<div>Financial Reporting Logic Coming Soon</div>} />
        </Route>
        <Route path="/accountant/forgot-password" element={<Forgotpass />} />

        {/* ================= EMPLOYEE PANEL (PROTECTED) ================= */}
        <Route 
          path="/employee" 
          element={
            <ProtectedRoute user={user} allowedRole="employee">
              <EmployeeLayout />
            </ProtectedRoute>
          }
        >
          <Route index element={<Navigate to="dashboard" replace />} />
          <Route path="dashboard" element={<EmployeeDashboard />} />
          <Route path="attendance" element={<AttendanceRecords />} />
          <Route path="leave" element={<LeaveManagement />} />
          <Route path="salary" element={<SalaryAnalytics />} />
          <Route path="settings" element={<Settings />} />
        </Route>
        <Route path="/employee/forgot-password" element={<ForgotPassword />} />

        {/* ================= 404 ERROR PAGE ================= */}
        <Route path="*" element={
          <div style={{ textAlign: "center", marginTop: "100px", fontFamily: "sans-serif" }}>
            <h1 style={{ fontSize: "72px", color: "#1e293b" }}>404</h1>
            <p style={{ color: "#64748b" }}>Access Denied or Page does not exist.</p>
            <button onClick={() => window.location.href = "/"} style={{ marginTop: "20px", padding: "10px 20px", cursor: "pointer" }}>Back to Home</button>
          </div>
        } />
      </Routes>
    </Router>
  );
}

export default App;