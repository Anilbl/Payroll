import React, { useState } from "react";
import "./System-Config.css";

export default function SystemConfig() {
  const initialConfig = [
    { id: 1, key: "Basic Salary", value: "30000", description: "Default basic salary for employees", icon: "💰" },
    { id: 2, key: "Hourly Rate", value: "150", description: "Overtime hourly rate", icon: "⏱️" },
    { id: 3, key: "Overtime Rate", value: "1.5", description: "Overtime multiplier", icon: "⚡" },
    { id: 4, key: "Tax Rate", value: "10", description: "Income tax percentage", icon: "📊" },
    { id: 5, key: "Allowance HRA", value: "5000", description: "Housing allowance", icon: "🏠" },
  ];

  const [config, setConfig] = useState(initialConfig);
  const [editingId, setEditingId] = useState(null);
  const [inputValue, setInputValue] = useState(""); // temporary input

  // When clicking edit, initialize inputValue with current config value
  const handleEdit = (item) => {
    setEditingId(item.id);
    setInputValue(item.value);
  };

  const handleChange = (e) => {
    setInputValue(e.target.value);
  };

  const handleUpdate = () => {
    setConfig(config.map(item => 
      item.id === editingId ? { ...item, value: inputValue } : item
    ));
    setEditingId(null);
    setInputValue("");
  };

  const handleCancel = () => {
    setEditingId(null);
    setInputValue("");
  };

  return (
    <div className="system-config-page">
      <header className="config-header">
        <h1>System Configuration</h1>
        <p>Manage payroll and salary parameters centrally</p>
      </header>

      <div className="config-container">
        {/* LEFT COLUMN: Cards */}
        <div className="config-cards">
          {config.map(item => (
            <div key={item.id} className="config-card">
              <div className="icon">{item.icon}</div>
              <h3>{item.key}</h3>
              <p>{item.value}</p>
            </div>
          ))}
        </div>

        {/* RIGHT COLUMN: Table */}
        <div className="config-table-container">
          <table className="config-table">
            <thead>
              <tr>
                <th>Parameter</th>
                <th>Value</th>
                <th>Description</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {config.map(item => (
                <tr key={item.id}>
                  <td>{item.key}</td>
                  <td>
                    <input
                      type="text"
                      value={editingId === item.id ? inputValue : item.value}
                      onChange={handleChange}
                    />
                  </td>
                  <td>{item.description}</td>
                  <td>
                    {editingId === item.id ? (
                      <>
                        <button className="update-btn" onClick={handleUpdate}>Update</button>
                        <button className="cancel-btn" onClick={handleCancel}>Cancel</button>
                      </>
                    ) : (
                      <button className="edit-btn" onClick={() => handleEdit(item)}>Edit</button>
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
}
