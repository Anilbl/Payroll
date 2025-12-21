import React, { useState, useEffect } from "react";
import "./Payroll.css";

const PayrollAdmin = () => {
  const [payrollData, setPayrollData] = useState([]);

  useEffect(() => {
    // Data structures mapped to 'employee', 'salary_grade', and 'leave_balance' tables
    const mockEmployees = [
      {
        id: 1,
        name: "Anil Bhul",
        marital_status: "Married",
        grade_name: "Grade A",
        basic_salary: 55000,
        allowance: 12000,
        pf_contribution: 5500, // 10% of basic
        cit_savings: 5000,     // Citizen Investment Trust
        life_insurance: 2000,  // Tax deductible up to Rs. 40,000/year
      },
      {
        id: 2,
        name: "Salina Bishwakarma",
        marital_status: "Unmarried",
        grade_name: "Grade B",
        basic_salary: 45000,
        allowance: 8000,
        pf_contribution: 4500,
        cit_savings: 0,
        life_insurance: 0,
      }
    ];
    setPayrollData(mockEmployees);
  }, []);

  const calculateNepalTax = (emp) => {
    const gross_salary = emp.basic_salary + emp.allowance;
    
    // Deductions allowed before tax as per Nepal Policy
    const total_deductions = emp.pf_contribution + emp.cit_savings + emp.life_insurance;
    const taxable_income = gross_salary - total_deductions;

    // Nepal Tax Slabs (Monthly Approximation)
    // Married: Rs. 50,000/mo tax-free | Unmarried: Rs. 41,666/mo tax-free
    const threshold = emp.marital_status === "Married" ? 50000 : 41666;
    
    // 1% Social Security Tax is mandatory for all salaried employees in Nepal
    let tax = taxable_income * 0.01;

    // Progressive tax logic (Simplified for demonstration)
    if (taxable_income > threshold) {
      tax += (taxable_income - threshold) * 0.10; // Next slab: 10%
    }

    const net_payable = gross_salary - (total_deductions + tax);

    return { gross_salary, total_deductions, tax, net_payable };
  };

  return (
    <div className="payroll-wrapper">
      <div className="payroll-header-section">
        <h2>Admin Payroll Management</h2>
        <p>Compliance: Nepal Labor Act & IRD Tax Policy (FY 2081/82)</p>
      </div>

      <div className="table-container">
        <table className="payroll-modern-table">
          <thead>
            <tr>
              <th>Employee Details</th>
              <th>Status</th>
              <th>Gross Salary</th>
              <th>Deductions (Savings)</th>
              <th>Gov Tax (1%+)</th>
              <th>Net Payable</th>
              <th>Action</th>
            </tr>
          </thead>
          <tbody>
            {payrollData.map((emp) => {
              const { gross_salary, total_deductions, tax, net_payable } = calculateNepalTax(emp);
              return (
                <tr key={emp.id}>
                  <td>
                    <div className="emp-info">
                      <span className="emp-name">{emp.name}</span>
                      <span className="emp-grade">{emp.grade_name}</span>
                    </div>
                  </td>
                  <td>
                    <span className={`status-badge ${emp.marital_status.toLowerCase()}`}>
                      {emp.marital_status}
                    </span>
                  </td>
                  <td className="amount">Rs. {gross_salary.toLocaleString()}</td>
                  <td className="amount deduction">Rs. {total_deductions.toLocaleString()}</td>
                  <td className="amount tax">Rs. {tax.toLocaleString()}</td>
                  <td className="amount net">Rs. {net_payable.toLocaleString()}</td>
                  <td>
                    <button className="btn-slip">Generate Slip</button>
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default PayrollAdmin;