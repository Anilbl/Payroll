package np.edu.nast.payroll.Payroll.repository;
import np.edu.nast.payroll.Payroll.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DepartmentRepository extends JpaRepository<Department, Integer> {

    // Add this line to fix the error on line 47
    Optional<Department> findByDeptName(String deptName);
}
