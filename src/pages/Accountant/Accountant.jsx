import React from "react";
import { Link } from "react-router-dom";
import "./accountant.css";

export default function Accountant() {
  return (
    <div className="dashboard-container">
      <aside className="sidebar">
        <div className="sidebar-header">
          <h2>Accountant Panel</h2>
        </div>

        <nav>
          <ul>
            <li className="active">
              <Link to="/accountant/dashboard">Dashboard</Link>
            </li>
            <li>
              <Link to="/accountant/payroll">Payroll</Link>
            </li>
            <li>
              <Link to="/accountant/salary">Salary</Link>
            </li>
            <li>
              <Link to="/accountant/tax">Tax</Link>
            </li>
            <li>
              <Link to="/accountant/reports">Reports</Link>
            </li>
          </ul>
        </nav>
      </aside>

      <main className="main-content">
        <header className="main-header">
          <h1>Accountant Dashboard</h1>
          <p>Payroll and Financial Management</p>
        </header>

        <div className="card-grid">
          <Link to="/accountant/payroll" className="dashboard-card">
            <div className="icon">💰</div>
            <h3>Payroll</h3>
            <p>Manage payroll</p>
          </Link>

          <Link to="/accountant/salary" className="dashboard-card">
            <div className="icon">📊</div>
            <h3>Salary</h3>
            <p>Process salaries</p>
          </Link>

          <Link to="/accountant/tax" className="dashboard-card">
            <div className="icon">🧾</div>
            <h3>Tax</h3>
            <p>Deductions & tax</p>
          </Link>

          <Link to="/accountant/reports" className="dashboard-card">
            <div className="icon">📈</div>
            <h3>Reports</h3>
            <p>Financial reports</p>
          </Link>
        </div>
      </main>
    </div>
  );
}
