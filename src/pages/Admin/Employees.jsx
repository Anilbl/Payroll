import { useState } from "react";
import "./Employees.css";

export default function Employees() {
  const [employees, setEmployees] = useState([
    {
      emp_id: 1,
      first_name: "Rahul",
      last_name: "Sharma",
      email: "rahul@example.com",
      contact: "9876543210",
      department: "IT",
      designation: "Developer",
      employment_status: "Active",
      joining_date: "2023-01-15",
    },
    {
      emp_id: 2,
      first_name: "Anita",
      last_name: "Patel",
      email: "anita@example.com",
      contact: "9876501234",
      department: "HR",
      designation: "Manager",
      employment_status: "Active",
      joining_date: "2022-05-20",
    },
  ]);

  const [editingId, setEditingId] = useState(null);
  const [formData, setFormData] = useState({});
  const [addingNew, setAddingNew] = useState(false);

  const startEdit = (emp) => {
    setEditingId(emp.emp_id);
    setFormData({ ...emp });
  };

  const cancelEdit = () => {
    setEditingId(null);
    setFormData({});
    setAddingNew(false);
  };

  const saveEdit = (id) => {
    if (addingNew) {
      // Assign a new unique emp_id
      const newId =
        employees.length > 0
          ? Math.max(...employees.map((e) => e.emp_id)) + 1
          : 1;
      setEmployees((prev) => [...prev, { ...formData, emp_id: newId }]);
    } else {
      setEmployees((prev) =>
        prev.map((emp) => (emp.emp_id === id ? { ...formData } : emp))
      );
    }
    cancelEdit();
  };

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const deleteEmployee = (id) => {
    if (window.confirm("Are you sure you want to delete this employee?")) {
      setEmployees((prev) => prev.filter((emp) => emp.emp_id !== id));
    }
  };

  return (
    <>
      <h1>Employees</h1>
      <button className="add-btn" onClick={() => { setAddingNew(true); setFormData({}); }}>
        Add New Employee
      </button>

      <table className="employee-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Name</th>
            <th>Email</th>
            <th>Contact</th>
            <th>Department</th>
            <th>Designation</th>
            <th>Status</th>
            <th>Joining Date</th>
            <th>Action</th>
          </tr>
        </thead>
        <tbody>
          {addingNew && (
            <tr>
              <td>New</td>
              <td>
                <input
                  name="first_name"
                  value={formData.first_name || ""}
                  onChange={handleChange}
                  placeholder="First Name"
                />
                <input
                  name="last_name"
                  value={formData.last_name || ""}
                  onChange={handleChange}
                  placeholder="Last Name"
                />
              </td>
              <td>
                <input
                  name="email"
                  value={formData.email || ""}
                  onChange={handleChange}
                  placeholder="Email"
                />
              </td>
              <td>
                <input
                  name="contact"
                  value={formData.contact || ""}
                  onChange={handleChange}
                  placeholder="Contact"
                />
              </td>
              <td>
                <input
                  name="department"
                  value={formData.department || ""}
                  onChange={handleChange}
                  placeholder="Department"
                />
              </td>
              <td>
                <input
                  name="designation"
                  value={formData.designation || ""}
                  onChange={handleChange}
                  placeholder="Designation"
                />
              </td>
              <td>
                <select
                  name="employment_status"
                  value={formData.employment_status || "Active"}
                  onChange={handleChange}
                >
                  <option value="Active">Active</option>
                  <option value="Inactive">Inactive</option>
                </select>
              </td>
              <td>
                <input
                  type="date"
                  name="joining_date"
                  value={formData.joining_date || ""}
                  onChange={handleChange}
                />
              </td>
              <td>
                <button onClick={() => saveEdit(null)}>Save</button>
                <button onClick={cancelEdit}>Cancel</button>
              </td>
            </tr>
          )}

          {employees.map((emp) => (
            <tr key={emp.emp_id}>
              <td>{emp.emp_id}</td>
              <td>
                {editingId === emp.emp_id ? (
                  <>
                    <input
                      name="first_name"
                      value={formData.first_name}
                      onChange={handleChange}
                      placeholder="First Name"
                    />
                    <input
                      name="last_name"
                      value={formData.last_name}
                      onChange={handleChange}
                      placeholder="Last Name"
                    />
                  </>
                ) : (
                  `${emp.first_name} ${emp.last_name}`
                )}
              </td>
              <td>
                {editingId === emp.emp_id ? (
                  <input
                    name="email"
                    value={formData.email}
                    onChange={handleChange}
                  />
                ) : (
                  emp.email
                )}
              </td>
              <td>
                {editingId === emp.emp_id ? (
                  <input
                    name="contact"
                    value={formData.contact}
                    onChange={handleChange}
                  />
                ) : (
                  emp.contact
                )}
              </td>
              <td>
                {editingId === emp.emp_id ? (
                  <input
                    name="department"
                    value={formData.department}
                    onChange={handleChange}
                  />
                ) : (
                  emp.department
                )}
              </td>
              <td>
                {editingId === emp.emp_id ? (
                  <input
                    name="designation"
                    value={formData.designation}
                    onChange={handleChange}
                  />
                ) : (
                  emp.designation
                )}
              </td>
              <td>
                {editingId === emp.emp_id ? (
                  <select
                    name="employment_status"
                    value={formData.employment_status}
                    onChange={handleChange}
                  >
                    <option value="Active">Active</option>
                    <option value="Inactive">Inactive</option>
                  </select>
                ) : (
                  emp.employment_status
                )}
              </td>
              <td>
                {editingId === emp.emp_id ? (
                  <input
                    type="date"
                    name="joining_date"
                    value={formData.joining_date}
                    onChange={handleChange}
                  />
                ) : (
                  emp.joining_date
                )}
              </td>
              <td>
                {editingId === emp.emp_id ? (
                  <>
                    <button onClick={() => saveEdit(emp.emp_id)}>Save</button>
                    <button onClick={cancelEdit}>Cancel</button>
                  </>
                ) : (
                  <>
                    <button onClick={() => startEdit(emp)}>Update</button>
                    <button onClick={() => deleteEmployee(emp.emp_id)}>
                      Delete
                    </button>
                  </>
                )}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </>
  );
}
