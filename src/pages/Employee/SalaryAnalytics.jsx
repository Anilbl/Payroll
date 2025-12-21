import React from "react";
import "./SalaryAnalytics.css";

const SalaryAnalytics = () => {
  const salaryData = {
    basic: 35000,
    allowances: 8000,
    deductions: 2000,
    net: 41000, // (35000 + 8000) - 2000
    month: "January 2025"
  };

  return (
    <div className="analytics-container">
      <div className="analytics-header">
        <div>
          <h1>Salary Analytics</h1>
          <p className="subtitle">Detailed breakdown for {salaryData.month}</p>
        </div>
        <button className="btn-download">
          <span className="icon">📄</span> Download PDF Payslip
        </button>
      </div>

      {/* Summary Card */}
      <div className="main-salary-card">
        <div className="net-salary-section">
          <label>Net Take-Home Pay</label>
          <h2 className="amount">Rs. {salaryData.net.toLocaleString()}</h2>
          <span className="status-tag">Paid on Jan 25, 2025</span>
        </div>
        
        <div className="salary-breakdown-grid">
          <div className="breakdown-item">
            <span className="label">Basic Salary</span>
            <span className="value">Rs. {salaryData.basic.toLocaleString()}</span>
          </div>
          <div className="breakdown-item">
            <span className="label">Allowances</span>
            <span className="value success">+ Rs. {salaryData.allowances.toLocaleString()}</span>
          </div>
          <div className="breakdown-item">
            <span className="label">Total Deductions</span>
            <span className="value danger">- Rs. {salaryData.deductions.toLocaleString()}</span>
          </div>
        </div>
      </div>

      {/* Statistics Section (Adds professionalism) */}
      <div className="details-grid">
        <div className="detail-card">
          <h3>Earnings Breakdown</h3>
          <div className="row"><span>Housing Allowance</span><span>Rs. 5,000</span></div>
          <div className="row"><span>Transport Allowance</span><span>Rs. 3,000</span></div>
        </div>
        <div className="detail-card">
          <h3>Tax & Deductions</h3>
          <div className="row"><span>Income Tax</span><span>Rs. 1,500</span></div>
          <div className="row"><span>Provident Fund</span><span>Rs. 500</span></div>
        </div>
      </div>
    </div>
  );
};

export default SalaryAnalytics;