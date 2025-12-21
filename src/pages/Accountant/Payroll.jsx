import React, { useState } from "react";
/* Using the Admin CSS for consistent professional grid styling */
import "../Admin/AdminDashboard.css"; 

const Payroll = () => {
  // Functional State: Managing the list of employees for the current cycle
  const [payrollList, setPayrollList] = useState([
    { id: "EMP101", name: "Anil Bhul", base: 45000, attendance: 28, status: "Verified" },
    { id: "EMP102", name: "Kabita Dhakal", base: 52000, attendance: 30, status: "Pending" },
    { id: "EMP103", name: "Bharat Gurdhami", base: 48000, attendance: 26, status: "Verified" },
    { id: "EMP104", name: "Salina B.K.", base: 50000, attendance: 29, status: "Pending" },
  ]);

  // Logic: Calculate Net Salary after 1% TDS (Tax) and Attendance pro-rata
  const calculateFinalPay = (base, days) => {
    const dailyRate = base / 30;
    const earned = dailyRate * days;
    const tax = earned * 0.01; // 1% Tax as per standard practice
    return (earned - tax).toLocaleString(undefined, { maximumFractionDigits: 0 });
  };

  // Function: Update individual status from 'Pending' to 'Verified'
  const handleVerify = (id) => {
    setPayrollList(prevList => 
      prevList.map(emp => 
        emp.id === id ? { ...emp, status: "Verified" } : emp
      )
    );
    alert(`Employee ${id} salary has been verified.`);
  };

  // Function: Bulk Action
  const processAll = () => {
    setPayrollList(prevList => prevList.map(emp => ({ ...emp, status: "Verified" })));
    alert("All pending salaries have been processed for this cycle.");
  };

  return (
    <div className="dashboard-view-container">
      <header className="overview-header" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <div>
          <h1>Payroll Processing</h1>
          <p className="subtitle">Accountant Portal | Current Cycle: November 2025</p>
        </div>
        <button 
          onClick={processAll}
          style={{ background: "#10b981", color: "white", padding: "10px 20px", borderRadius: "8px", border: "none", cursor: "pointer", fontWeight: "600" }}
        >
          Verify All Pending
        </button>
      </header>

      <div className="stats-grid" style={{ gridTemplateColumns: "1fr", display: "block" }}>
        <div className="pro-stat-card" style={{ padding: "24px", cursor: "default", overflowX: "auto" }}>
          <table style={{ width: "100%", borderCollapse: "collapse" }}>
            <thead>
              <tr style={{ textAlign: "left", color: "#64748b", borderBottom: "2px solid #f1f5f9" }}>
                <th style={{ padding: "12px" }}>Emp ID</th>
                <th>Employee Name</th>
                <th>Base Salary</th>
                <th>Attendance</th>
                <th>Net Payable (NPR)</th>
                <th>Status</th>
                <th>Action</th>
              </tr>
            </thead>
            <tbody>
              {payrollList.map((emp) => (
                <tr key={emp.id} style={{ borderBottom: "1px solid #f1f5f9" }}>
                  <td style={{ padding: "16px", fontWeight: "600" }}>{emp.id}</td>
                  <td style={{ fontWeight: "500" }}>{emp.name}</td>
                  <td>{emp.base.toLocaleString()}</td>
                  <td>{emp.attendance}/30 Days</td>
                  <td style={{ fontWeight: "700", color: "#1e293b" }}>
                    Rs. {calculateFinalPay(emp.base, emp.attendance)}
                  </td>
                  <td>
                    <span style={{ 
                      padding: "4px 12px", 
                      borderRadius: "20px", 
                      fontSize: "12px", 
                      fontWeight: "600",
                      background: emp.status === "Verified" ? "#dcfce7" : "#fef3c7",
                      color: emp.status === "Verified" ? "#15803d" : "#b45309"
                    }}>
                      {emp.status}
                    </span>
                  </td>
                  <td>
                    {emp.status === "Pending" ? (
                      <button 
                        onClick={() => handleVerify(emp.id)}
                        style={{ background: "#6366f1", color: "white", border: "none", padding: "8px 16px", borderRadius: "6px", cursor: "pointer" }}
                      >
                        Verify
                      </button>
                    ) : (
                      <span style={{ color: "#94a3b8", fontSize: "13px" }}>Completed</span>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};

export default Payroll;