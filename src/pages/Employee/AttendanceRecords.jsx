import React, { useState } from "react";
import "./EmployeeDashboard.css";

const AttendanceRecords = () => {
  const [status, setStatus] = useState("Not Checked In");
  const [location, setLocation] = useState(null);
  const [loading, setLoading] = useState(false);

  // Mock data - in a real app, this would come from your backend/Firebase
  const [history, setHistory] = useState([
    { date: "2025-01-15", in: "09:10 AM", out: "05:30 PM", status: "Present" },
    { date: "2025-01-14", in: "—", out: "—", status: "Absent" },
  ]);

  const handleAttendance = (type) => {
    setLoading(true);
    if ("geolocation" in navigator) {
      navigator.geolocation.getCurrentPosition(
        (position) => {
          const { latitude, longitude } = position.coords;
          const time = new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
          const date = new Date().toISOString().split('T')[0];

          setLocation(`${latitude.toFixed(4)}, ${longitude.toFixed(4)}`);
          setStatus(type === "in" ? "Checked In" : "Checked Out");
          
          // Logic to update history table (Mock)
          if(type === "in") {
             setHistory([{ date, in: time, out: "—", status: "Present" }, ...history]);
          }

          setLoading(false);
          alert(`Attendance ${type === 'in' ? 'In' : 'Out'} successful! \nCoords: ${latitude}, ${longitude}`);
        },
        (error) => {
          alert("Error: Please enable location services to take attendance.");
          setLoading(false);
        }
      );
    }
  };

  return (
    <div className="page-container">
      <div className="header-section">
        <h1>Attendance Records</h1>
        <p className="subtitle">Manage your daily clock-in/out with GPS verification.</p>
      </div>

      {/* Self-Attendance Action Card */}
      <div className="attendance-action-card">
        <div className="action-info">
          <h3>Daily Check-In</h3>
          <span className={`status-badge ${status.replace(/\s+/g, '-').toLowerCase()}`}>
            {status}
          </span>
          {location && <p className="gps-text">📍 Verified: {location}</p>}
        </div>
        
        <div className="button-group">
          <button 
            className="btn-primary" 
            onClick={() => handleAttendance("in")}
            disabled={loading || status === "Checked In"}
          >
            {loading ? "Verifying..." : "Check In"}
          </button>
          <button 
            className="btn-outline" 
            onClick={() => handleAttendance("out")}
            disabled={loading || status === "Not Checked In" || status === "Checked Out"}
          >
            Check Out
          </button>
        </div>
      </div>

      {/* History Table */}
      <div className="table-container">
        <table className="data-table">
          <thead>
            <tr>
              <th>Date</th>
              <th>Check In</th>
              <th>Check Out</th>
              <th>Status</th>
            </tr>
          </thead>
          <tbody>
            {history.map((row, index) => (
              <tr key={index}>
                <td>{row.date}</td>
                <td>{row.in}</td>
                <td>{row.out}</td>
                <td>
                  <span className={`pill ${row.status.toLowerCase()}`}>
                    {row.status}
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

export default AttendanceRecords;