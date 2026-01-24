package np.edu.nast.payroll.Payroll.service;

import np.edu.nast.payroll.Payroll.entity.Employee;
import java.util.List;
import java.util.Map;

public interface EmployeeService {
    Employee create(Employee employee);
    Employee update(Integer id, Employee employee);
    void delete(Integer id);

    Employee getById(Integer id);
    Employee getByEmail(String email);

    // NEW FEATURE: Concept change to User ID
    Employee getByUserId(Integer userId);

    List<Employee> getAll();
    Map<Integer, Long> getActiveEmployeeStats();
    Map<String, Object> getDashboardStats(Integer id);
}