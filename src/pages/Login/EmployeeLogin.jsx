import { useState } from "react";
import { useNavigate } from "react-router-dom"; 
import "./login.css"; 

export default function EmployeeLogin({ setUser }) {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const navigate = useNavigate(); 

  const handleSubmit = (e) => {
    e.preventDefault();
    // Example credentials - update these based on your database
    if (email && password) {
      const employeeSession = { role: "employee", email: email };
      localStorage.setItem("user_session", JSON.stringify(employeeSession));
      setUser(employeeSession);
      navigate("/employee/dashboard"); 
    }
  };

  return (
    <div className="auth-container">
      <div className="auth-card">
        <div className="auth-header">
          <div className="auth-logo">NAST</div>
          <h2>Employee Portal</h2>
          <p>Access your payroll and attendance</p>
        </div>

        <form onSubmit={handleSubmit} className="auth-form">
          <div className="form-group">
            <label>Employee Email</label>
            <input
              type="email"
              placeholder="employee@nast.edu.np"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
            />
          </div>

          <div className="form-group">
            <label>Password</label>
            <input
              type="password"
              placeholder="••••••••"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />
          </div>

          <button type="submit" className="btn-primary" style={{ backgroundColor: '#059669' }}>
            Sign In
          </button>
        </form>

        <div className="auth-footer">
          <button onClick={() => navigate("/")} className="btn-text">
            ← Back to Landing Page
          </button>
          <a href="/employee/forgot-password">Forgot Password?</a>
        </div>
      </div>
    </div>
  );
}