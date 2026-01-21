package np.edu.nast.payroll.Payroll.service;

import np.edu.nast.payroll.Payroll.dto.auth.DashboardStatsDTO;
import np.edu.nast.payroll.Payroll.entity.Employee;
import java.util.List;
import java.util.Map;

public interface EmployeeService {
    DashboardStatsDTO getEmployeeStatsByUserId(Integer userId);
    Employee create(Employee employee);
    Employee update(Integer id, Employee employee);

    // This will now handle the soft-delete logic
    void delete(Integer id);

    Employee getById(Integer id);
    List<Employee> getAll();
    Map<Integer, Long> getActiveEmployeeStats();
}