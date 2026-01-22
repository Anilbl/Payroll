package np.edu.nast.payroll.Payroll.service;

import np.edu.nast.payroll.Payroll.dto.auth.DashboardStatsDTO;
import np.edu.nast.payroll.Payroll.entity.Employee;
import java.util.List;
import java.util.Map;

public interface EmployeeService {
    Employee create(Employee employee);
    List<Employee> getAll();
    Employee getById(Integer id);
    Employee update(Integer id, Employee employee);
    void delete(Integer id);
    DashboardStatsDTO getEmployeeStatsByUserId(Integer userId);
    Map<Integer, Long> getActiveEmployeeStats();
}