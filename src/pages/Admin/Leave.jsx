import React, { useState, useEffect } from "react";
import "./Leave.css";

const LeaveAdmin = () => {
  const [leaveRequests, setLeaveRequests] = useState([]);

  useEffect(() => {
    // Standard Nepal Annual Leave: ~15-18 Home Leave + 12 Sick Leave = ~30 Total
    const data = [
      {
        leave_id: 1,
        emp_name: "John Doe",
        leave_type: "Home Leave",
        start_date: "2025-12-22",
        end_date: "2025-12-25",
        total_days: 4,
        status: "Pending",
        total_taken: 0,
        total_allowed: 30, // Default for Nepal Labor Law
      },
      {
        leave_id: 2,
        emp_name: "Jane Smith",
        leave_type: "Sick Leave",
        start_date: "2025-12-10",
        end_date: "2025-12-12",
        total_days: 3,
        status: "Pending",
        total_taken: 0,
        total_allowed: 30,
      },
    ];
    setLeaveRequests(data);
  }, []);

  const handleLeaveAction = (leave_id, action) => {
    setLeaveRequests((prev) =>
      prev.map((leave) => {
        if (leave.leave_id === leave_id) {
          const isApproving = action === "Approved";
          return {
            ...leave,
            status: action,
            // Logic: Increase 'total_taken' and decrease 'remaining'
            total_taken: isApproving 
              ? leave.total_taken + leave.total_days 
              : leave.total_taken,
          };
        }
        return leave;
      })
    );
  };

  return (
    <div className="leave-container">
      <h2 className="leave-header">Employee Leave Management (Nepal Standards)</h2>
      <table className="leave-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Employee</th>
            <th>Leave Type</th>
            <th>Dates</th>
            <th>Requested</th>
            <th>Status</th>
            <th>Total Taken</th>
            <th>Remaining Days</th> {/* Renamed from Remaining Balance */}
            <th>Action</th>
          </tr>
        </thead>
        <tbody>
          {leaveRequests.map((leave) => (
            <tr key={leave.leave_id}>
              <td>{leave.leave_id}</td>
              <td>{leave.emp_name}</td>
              <td>{leave.leave_type}</td>
              <td>{leave.start_date} to {leave.end_date}</td>
              <td>{leave.total_days} Days</td>
              <td>
                <span className={`status-badge ${leave.status.toLowerCase()}`}>
                  {leave.status}
                </span>
              </td>
              <td>{leave.total_taken}</td>
              {/* FIXED: Calculates Remaining Days dynamically to avoid NaN */}
              <td className="remaining-cell">
                {leave.total_allowed - leave.total_taken}
              </td>
              <td>
                {leave.status === "Pending" ? (
                  <div className="btn-group">
                    <button className="btn-approve" onClick={() => handleLeaveAction(leave.leave_id, "Approved")}>Approve</button>
                    <button className="btn-reject" onClick={() => handleLeaveAction(leave.leave_id, "Rejected")}>Reject</button>
                  </div>
                ) : (
                  <span className={`text-final ${leave.status.toLowerCase()}`}>{leave.status}</span>
                )}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default LeaveAdmin;