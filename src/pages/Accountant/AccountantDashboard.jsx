import React from "react";
import { useNavigate } from "react-router-dom";
/* Ensure this path is correct: from src/pages/Accountant/ to src/pages/Admin/ */
import "../Admin/AdminDashboard.css"; 

const AccountantDashboard = () => {
  const navigate = useNavigate();

  const accStats = [
    { label: "Pending Payroll", value: "Nov 2025", sub: "Monthly Cycle", icon: "💰", color: "#6366f1", path: "accountant/dashboard" },
    { label: "Salary Verified", value: "142/154", sub: "Employee Records", icon: "✅", color: "#10b981", path: "accountant/dashboard" },
    { label: "Total Deductions", value: "Rs. 42K", sub: "Tax & Insurance", icon: "📉", color: "#ef4444", path: "accountant/dashboard" },
    { label: "Annual Budget", value: "1.02 Lakh", sub: "Project Total", icon: "🏦", color: "#3b82f6", path: "accountant/dashboard" },
    { label: "Salary Slips", value: "Generated", sub: "Oct 2025 Batch", icon: "📄", color: "#f59e0b", path: "accountant/dashboard" },
    { label: "System Alerts", value: "02", sub: "Audit Required", icon: "🔔", color: "#64748b", path: "accountant/dashboard" }
  ];

  return (
    <div className="dashboard-view-container">
      <div className="overview-header">
        <h1>Financial Dashboard</h1>
        <p className="subtitle">Operational payroll data - Accountant Access Only</p>
      </div>
      <div className="stats-grid">
        {accStats.map((stat, i) => (
          <div key={i} className="pro-stat-card" onClick={() => navigate(`/${stat.path}`)}>
            <div className="card-top-row">
              <div className="card-info">
                <span className="stat-label">{stat.label}</span>
                <h2 className="stat-value">{stat.value}</h2>
                <span className="stat-subtext">{stat.sub}</span>
              </div>
              <div className="stat-icon-wrapper" style={{ backgroundColor: `${stat.color}15`, color: stat.color }}>
                {stat.icon}
              </div>
            </div>
            <div className="card-accent-bar">
              <div className="accent-fill" style={{ width: '100%', backgroundColor: stat.color, height: '100%' }}></div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default AccountantDashboard;