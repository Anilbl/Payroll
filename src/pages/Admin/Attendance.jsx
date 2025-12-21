import { useEffect, useState } from "react";
import "./Attendance.css";

// Sample employee and attendance data
const employeesData = [
  { emp_id: 1, first_name: "Rahul", last_name: "Sharma" },
  { emp_id: 2, first_name: "Anita", last_name: "Patel" },
  { emp_id: 3, first_name: "John", last_name: "Doe" },
];

const attendanceData = [
  { emp_id: 1, date: "2025-12-17", status: "Present" },
  { emp_id: 1, date: "2025-12-18", status: "Absent" },
  { emp_id: 1, date: "2025-12-19", status: "Present" },
  { emp_id: 2, date: "2025-12-17", status: "Leave" },
  { emp_id: 2, date: "2025-12-18", status: "Present" },
  { emp_id: 3, date: "2025-12-17", status: "Absent" },
  { emp_id: 3, date: "2025-12-18", status: "Present" },
];

export default function AdminAttendanceOverview() {
  const [selectedEmployee, setSelectedEmployee] = useState("");
  const [employeeHistory, setEmployeeHistory] = useState([]);

  useEffect(() => {
    if (selectedEmployee) {
      const empHistory = attendanceData.filter(
        (a) => a.emp_id === parseInt(selectedEmployee)
      );
      setEmployeeHistory(empHistory);
    } else {
      setEmployeeHistory([]);
    }
  }, [selectedEmployee]);

  const summary = employeeHistory.reduce(
    (acc, curr) => {
      if (curr.status === "Present") acc.present += 1;
      if (curr.status === "Absent") acc.absent += 1;
      if (curr.status === "Leave") acc.leave += 1;
      return acc;
    },
    { present: 0, absent: 0, leave: 0 }
  );

  return (
    <div className="attendance-container">
      <h1>Admin - Employee Attendance Overview</h1>

      <div className="attendance-header">
        <label>
          Select Employee:{" "}
          <select
            value={selectedEmployee}
            onChange={(e) => setSelectedEmployee(e.target.value)}
          >
            <option value="">-- Select Employee --</option>
            {employeesData.map((emp) => (
              <option key={emp.emp_id} value={emp.emp_id}>
                {emp.first_name} {emp.last_name}
              </option>
            ))}
          </select>
        </label>
      </div>

      {selectedEmployee && (
        <>
          <h2>Attendance History</h2>
          <table className="attendance-table">
            <thead>
              <tr>
                <th>Date</th>
                <th>Status</th>
              </tr>
            </thead>
            <tbody>
              {employeeHistory.map((record, index) => (
                <tr key={index}>
                  <td>{record.date}</td>
                  <td>{record.status}</td>
                </tr>
              ))}
            </tbody>
          </table>

          <div className="attendance-summary">
            <h3>Summary:</h3>
            <p>Present: {summary.present}</p>
            <p>Absent: {summary.absent}</p>
            <p>Leave: {summary.leave}</p>
          </div>
        </>
      )}
    </div>
  );
}
