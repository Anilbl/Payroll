import React from "react";
import { Bar } from "react-chartjs-2";
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  BarElement,
  Tooltip,
  Legend
} from "chart.js";
import "./Report.css";

ChartJS.register(CategoryScale, LinearScale, BarElement, Tooltip, Legend);

export default function Report() {
  const monthlyPayrollData = {
    labels: ["Jan", "Feb", "Mar", "Apr", "May", "Jun"],
    datasets: [
      {
        label: "Total Salary Paid (NPR)",
        data: [1200000, 1350000, 1280000, 1400000, 1500000, 1450000],
        backgroundColor: "#1976d2"
      }
    ]
  };

  return (
    <div className="report-page">

      {/* LEFT COLUMN */}
      <div className="report-left">
        <header className="report-header">
          <h1>Payroll Reports</h1>
          <p>Consolidated payroll, employee and financial insights</p>
        </header>

        <div className="report-summary">
          <div className="summary-card">
            <div className="icon">👥</div>
            <h3>Total Employees</h3>
            <p>128</p>
          </div>

          <div className="summary-card">
            <div className="icon">💰</div>
            <h3>Monthly Payroll</h3>
            <p>NPR 1,450,000</p>
          </div>

          <div className="summary-card">
            <div className="icon">📉</div>
            <h3>Total Deductions</h3>
            <p>NPR 220,000</p>
          </div>

          <div className="summary-card">
            <div className="icon">🧾</div>
            <h3>Total Allowances</h3>
            <p>NPR 310,000</p>
          </div>

          <div className="summary-card">
            <div className="icon">🕒</div>
            <h3>Pending Leaves</h3>
            <p>9</p>
          </div>
        </div>
      </div>

      {/* RIGHT COLUMN */}
      <div className="report-right">
        <div className="chart-section">
          <h2>Monthly Payroll Expenditure</h2>
          <Bar
            data={monthlyPayrollData}
            options={{
              responsive: true,
              maintainAspectRatio: false,
              plugins: {
                legend: {
                  display: true,
                  position: "bottom",
                }
              }
            }}
            height={180} // small fixed height to fit screen
          />
        </div>
      </div>

    </div>
  );
}
