package np.edu.nast.payroll.Payroll.repository;

import np.edu.nast.payroll.Payroll.entity.Employee;
import np.edu.nast.payroll.Payroll.entity.EmployeeLeave;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EmployeeLeaveRepository extends JpaRepository<EmployeeLeave, Integer> {

    // Standard JPA method to find by the ID of the nested Employee entity
    List<EmployeeLeave> findByEmployee_EmpId(Integer empId);

    List<EmployeeLeave> findAllByEmployee(Employee employee);

    long countByEmployeeEmpIdAndStatus(Integer empId, String status);

    long countByStatus(String status);
}