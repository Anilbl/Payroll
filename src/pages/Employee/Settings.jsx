import React, { useState } from "react";
import "./Settings.css";

const Settings = () => {
  const [isNotificationsEnabled, setNotifications] = useState(true);

  const user = {
    name: "John Doe",
    id: "EMP-2025-081",
    email: "john.doe@company.com",
    department: "Software Engineering",
    joinDate: "January 15, 2023"
  };

  return (
    <div className="settings-container">
      <div className="settings-header">
        <h1>Account Settings</h1>
        <p className="subtitle">Manage your profile and security preferences</p>
      </div>

      <div className="settings-grid">
        {/* LEFT COLUMN: Profile Info */}
        <div className="settings-card profile-card">
          <div className="profile-header">
            <div className="profile-avatar">{user.name.charAt(0)}</div>
            <h3>{user.name}</h3>
            <p className="role-tag">{user.department}</p>
          </div>
          
          <div className="info-list">
            <div className="info-item">
              <label>Employee ID</label>
              <span>{user.id}</span>
            </div>
            <div className="info-item">
              <label>Email Address</label>
              <span>{user.email}</span>
            </div>
            <div className="info-item">
              <label>Joined Date</label>
              <span>{user.joinDate}</span>
            </div>
          </div>
        </div>

        {/* RIGHT COLUMN: Options */}
        <div className="settings-card options-card">
          <section className="settings-section">
            <h3>Security</h3>
            <div className="setting-option">
              <div>
                <p className="option-title">Change Password</p>
                <p className="option-desc">Update your login credentials regularly</p>
              </div>
              <button className="btn-outline">Update</button>
            </div>
          </section>

          <section className="settings-section">
            <h3>Preferences</h3>
            <div className="setting-option">
              <div>
                <p className="option-title">Email Notifications</p>
                <p className="option-desc">Receive payslip alerts and leave updates</p>
              </div>
              <label className="switch">
                <input 
                  type="checkbox" 
                  checked={isNotificationsEnabled} 
                  onChange={() => setNotifications(!isNotificationsEnabled)} 
                />
                <span className="slider round"></span>
              </label>
            </div>
          </section>

          <section className="settings-section danger-zone">
            <h3>Session Management</h3>
            <div className="setting-option">
              <div>
                <p className="option-title">Account Security</p>
                <p className="option-desc">Sign out from all active devices</p>
              </div>
              <button className="btn-danger-outline">Logout</button>
            </div>
          </section>
        </div>
      </div>
    </div>
  );
};

export default Settings;