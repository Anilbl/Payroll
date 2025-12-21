import React from "react";
import { useNavigate } from "react-router-dom";
import "./AdminDashboard.css";

const AdminDashboard = () => {
  const navigate = useNavigate();

  const stats = [
    { label: "Total Workforce", value: "154", sub: "Total Employees", icon: "👥", color: "#3b82f6", path: "admin/employees" },
    { label: "Daily Presence", value: "92%", sub: "Active Today", icon: "🕒", color: "#10b981", path: "admin/attendance" },
    { label: "Pending Leave", value: "08", sub: "New Requests", icon: "📝", color: "#f59e0b", path: "admin/leave" },
    { label: "Payroll Status", value: "Rs. 1.2M", sub: "Dec 2024 Total", icon: "💰", color: "#6366f1", path: "admin/payroll" },
    { label: "System Config", value: "Active", sub: "Settings & Rules", icon: "⚙️", color: "#64748b", path: "admin/system-config" },
    { label: "System Reports", value: "24 New", sub: "Financial Data", icon: "📊", color: "#ec4899", path: "admin/report" }
  ];

  return (
    <div className="dashboard-view-container">
      <div className="overview-header">
        <h1>Dashboard Overview</h1>
        <p className="subtitle">Real-time summary of the NAST Payroll Management System</p>
      </div>

      <div className="stats-grid">
        {stats.map((stat, i) => (
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

export default AdminDashboard;