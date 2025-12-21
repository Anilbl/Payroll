import React from "react";
import "./EmployeeDashboard.css";

const EmployeeDashboard = () => {
  // Mock data - in a real app, this comes from your Auth session or API
  const stats = [
    { label: "Attendance", value: "98%", icon: "🕒", trend: "+2% from last month" },
    { label: "Leave Balance", value: "12 Days", icon: "📝", trend: "Refreshes in 3 months" },
    { label: "Net Salary", value: "Rs. 45,000", icon: "💰", trend: "Latest: Dec 2024" },
  ];

  return (
    <div className="dashboard-content">
      {/* Header Section */}
      <header className="dashboard-header">
        <div className="welcome-text">
          <h1>Welcome Back, John! 👋</h1>
          <p>You have 2 pending leave approvals and your latest payslip is ready.</p>
        </div>
      </header>

      {/* Stats Overview */}
      <div className="stats-grid">
        {stats.map((stat, index) => (
          <div key={index} className="stat-card">
            <div className="stat-icon">{stat.icon}</div>
            <div className="stat-info">
              <span className="stat-label">{stat.label}</span>
              <span className="stat-value">{stat.value}</span>
              <span className="stat-trend">{stat.trend}</span>
            </div>
          </div>
        ))}
      </div>

      {/* Main Actions Area */}
      <div className="actions-section">
        <h2>Quick Actions</h2>
        <div className="action-grid">
          <button className="action-tile">
            <span className="tile-icon">⏰</span>
            <div>
              <h3>Clock-in / Out</h3>
              <p>Record your daily attendance</p>
            </div>
          </button>
          <button className="action-tile">
            <span className="tile-icon">📅</span>
            <div>
              <h3>Apply for Leave</h3>
              <p>Request time off or sick leave</p>
            </div>
          </button>
          <button className="action-tile">
            <span className="tile-icon">📄</span>
            <div>
              <h3>View Payslip</h3>
              <p>Download salary breakdowns</p>
            </div>
          </button>
          <button className="action-tile">
            <span className="tile-icon">⚙️</span>
            <div>
              <h3>Profile Settings</h3>
              <p>Update personal information</p>
            </div>
          </button>
        </div>
      </div>
    </div>
  );
};

export default EmployeeDashboard;