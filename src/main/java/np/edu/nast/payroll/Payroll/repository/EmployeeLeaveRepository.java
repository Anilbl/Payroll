package np.edu.nast.payroll.Payroll.repository;

import np.edu.nast.payroll.Payroll.entity.EmployeeLeave;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EmployeeLeaveRepository extends JpaRepository<EmployeeLeave, Integer> {

    // Exact match for the Service call in getLeavesByEmployee
    List<EmployeeLeave> findAllByEmployee_EmpId(Integer empId);

    long countByStatus(String status);
}