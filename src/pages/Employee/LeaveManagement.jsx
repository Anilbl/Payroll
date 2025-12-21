import React, { useState } from "react";
import "./LeaveManagement.css";

const LeaveManagement = () => {
  // Initial Balance
  const [leaveBalance, setLeaveBalance] = useState(15);
  const totalEntitled = 20; // Used for progress bar calculation

  // Form State
  const [formData, setFormData] = useState({
    type: "Annual Leave",
    days: 1,
    reason: "",
  });

  // History State
  const [history, setHistory] = useState([
    { id: 1, type: "Sick Leave", days: 2, status: "approved", date: "2025-01-10" },
    { id: 2, type: "Annual Leave", days: 3, status: "approved", date: "2024-12-15" },
  ]);

  const handleSubmit = (e) => {
    e.preventDefault();
    const requestedDays = parseInt(formData.days);

    // Logic: Decrease balance if request is valid
    if (requestedDays > leaveBalance) {
      alert("Error: You do not have enough leave days remaining.");
      return;
    }

    // 1. Decrease the count
    setLeaveBalance((prev) => prev - requestedDays);

    // 2. Add to history table
    const newRequest = {
      id: Date.now(),
      type: formData.type,
      days: requestedDays,
      status: "pending",
      date: new Date().toISOString().split("T")[0],
    };

    setHistory([newRequest, ...history]);

    // 3. Reset Form
    setFormData({ ...formData, reason: "", days: 1 });
    alert("Leave request submitted successfully!");
  };

  return (
    <div className="page-container">
      <div className="header-section">
        <h1>Leave Management</h1>
       
      </div>

      <div className="leave-grid">
        {/* LEFT: Balance Card */}
        <div className="balance-card">
          <div className="balance-info">
            <span className="label">Days Remaining</span>
            <div className="balance-value">{leaveBalance}</div>
            <span className="unit">Out of {totalEntitled} Days Total</span>
          </div>
          <div className="progress-bar-container">
            <div
              className="progress-fill"
              style={{ width: `${(leaveBalance / totalEntitled) * 100}%` }}
            ></div>
          </div>
        </div>

        {/* RIGHT: Request Form */}
        <div className="request-card">
          <h3>New Leave Request</h3>
          <form onSubmit={handleSubmit}>
            <div className="form-row">
              <div className="input-group">
                <label>Leave Type</label>
                <select
                  value={formData.type}
                  onChange={(e) => setFormData({ ...formData, type: e.target.value })}
                >
                  <option>Annual Leave</option>
                  <option>Sick Leave</option>
                  <option>Casual Leave</option>
                  <option>Maternity/Paternity</option>
                </select>
              </div>
              <div className="input-group">
                <label>Duration (Days)</label>
                <input
                  type="number"
                  min="1"
                  max={leaveBalance}
                  value={formData.days}
                  onChange={(e) => setFormData({ ...formData, days: e.target.value })}
                  required
                />
              </div>
            </div>
            <div className="input-group">
              <label>Reason for Leave</label>
              <textarea
                rows="4"
                placeholder="Briefly describe your reason..."
                value={formData.reason}
                onChange={(e) => setFormData({ ...formData, reason: e.target.value })}
                required
              ></textarea>
            </div>
            <button type="submit" className="btn-primary full-width">
              Submit Leave Request
            </button>
          </form>
        </div>
      </div>

      {/* BOTTOM: History Table */}
      <div className="table-container" style={{ marginTop: "2.5rem" }}>
        <h3>Leave History</h3>
        <table className="data-table">
          <thead>
            <tr>
              <th>Request Date</th>
              <th>Type</th>
              <th>Days</th>
              <th>Status</th>
            </tr>
          </thead>
          <tbody>
            {history.map((item) => (
              <tr key={item.id}>
                <td>{item.date}</td>
                <td>{item.type}</td>
                <td>{item.days} Day(s)</td>
                <td>
                  <span className={`pill ${item.status}`}>
                    {item.status.toUpperCase()}
                  </span>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default LeaveManagement;