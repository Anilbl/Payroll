import React from "react";
import { Outlet, NavLink, useNavigate } from "react-router-dom";
/* Relative path fix: points to Admin CSS for consistent branding */
import "../pages/Admin/AdminLayout.css"; 

const AccountantLayout = () => {
  const navigate = useNavigate();

  const handleLogout = () => {
    localStorage.removeItem("user_session");
    navigate("/");
  };

  // Synchronized paths with App.js routes
  const menuItems = [
    { path: "dashboard", label: "Financial Overview", icon: "📊" },
    { path: "payroll", label: "Payroll Processing", icon: "💸" }, // Changed to 'payroll'
    { path: "tax-verification", label: "Tax & Deductions", icon: "🔍" },
    { path: "reports", label: "Financial Reports", icon: "📈" }
  ];

  return (
    <div className="admin-container">
      <aside className="admin-sidebar">
        <div className="sidebar-header">
          <div className="logo-box">NAST</div>
          <h2 className="panel-title">Accountant</h2>
        </div>

        <nav className="sidebar-nav">
          {menuItems.map((item) => (
            <NavLink 
              key={item.path} 
              to={item.path} 
              className={({ isActive }) => isActive ? "nav-item active" : "nav-item"}
            >
              <span className="nav-icon">{item.icon}</span>
              <span className="nav-label">{item.label}</span>
            </NavLink>
          ))}
        </nav>

        <button className="signout-btn" onClick={handleLogout}>
          Sign Out
        </button>
      </aside>

      <main className="admin-main">
        <header className="admin-top-bar">
          <div className="search-wrapper">
            <input type="text" placeholder="Search budget records..." />
          </div>
          <div className="admin-profile">
            <div className="profile-info">
              <span className="name">Finance Dept</span>
              <span className="role">Accountant Portal</span>
            </div>
            <div className="avatar">AC</div>
          </div>
        </header>

        <div className="admin-content">
          {/* This renders the AccountantDashboard or Payroll component */}
          <Outlet /> 
        </div>
      </main>
    </div>
  );
};

export default AccountantLayout;