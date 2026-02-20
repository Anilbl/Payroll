package np.edu.nast.payroll.Payroll.repository;

import np.edu.nast.payroll.Payroll.entity.Employee;
import np.edu.nast.payroll.Payroll.entity.EmployeeLeave;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EmployeeLeaveRepository extends JpaRepository<EmployeeLeave, Integer> {

    /**
     * Optimized Overlap Query:
     * Finds leaves where the leave period touches the payroll period.
     */
    @Query("SELECT l FROM EmployeeLeave l WHERE l.employee.empId = :empId " +
            "AND l.status = :status " +
            "AND l.startDate <= :periodEnd " +
            "AND l.endDate >= :periodStart")
    List<EmployeeLeave> findRelevantLeaves(
            @Param("empId") Integer empId,
            @Param("status") String status,
            @Param("periodStart") LocalDate periodStart,
            @Param("periodEnd") LocalDate periodEnd
    );

    // Kept for backward compatibility
    List<EmployeeLeave> findByEmployeeEmpIdAndStatusAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            Integer empId, String status, LocalDate periodEnd, LocalDate periodStart
    );

    List<EmployeeLeave> findByEmployee_EmpId(Integer empId);
    List<EmployeeLeave> findAllByEmployee(Employee employee);
    long countByEmployeeEmpIdAndStatus(Integer empId, String status);
    long countByStatus(String status);
}