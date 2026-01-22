package np.edu.nast.payroll.Payroll.service;

<<<<<<< HEAD
import np.edu.nast.payroll.Payroll.dto.auth.DashboardStatsDTO;
=======
>>>>>>> 3214be41b790e5d207ff8a4a5185d56a25676df5
import np.edu.nast.payroll.Payroll.entity.Employee;
import java.util.List;
import java.util.Map;

public interface EmployeeService {
<<<<<<< HEAD
    DashboardStatsDTO getEmployeeStatsByUserId(Integer userId);
    Employee create(Employee employee);
    Employee update(Integer id, Employee employee);

    // This will now handle the soft-delete logic
    void delete(Integer id);

    Employee getById(Integer id);
    List<Employee> getAll();
    Map<Integer, Long> getActiveEmployeeStats();
}
=======
    Employee create(Employee employee);
    Employee update(Integer id, Employee employee);
    void delete(Integer id);
    Employee getById(Integer id);
    List<Employee> getAll();

    // New method for stats
    Map<Integer, Long> getActiveEmployeeStats();
}
>>>>>>> 3214be41b790e5d207ff8a4a5185d56a25676df5
