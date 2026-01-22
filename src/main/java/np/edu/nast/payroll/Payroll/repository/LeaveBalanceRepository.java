package np.edu.nast.payroll.Payroll.repository;

import np.edu.nast.payroll.Payroll.entity.LeaveBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface LeaveBalanceRepository extends JpaRepository<LeaveBalance, Integer> {

    // --- Your Local Changes (Logic for Leave Approvals & Dashboard) ---

    // Find balances by employee ID
    List<LeaveBalance> findByEmployee_EmpId(Integer empId);

    // Find specific balance for a type and year (used for updating balances)
    Optional<LeaveBalance> findByEmployee_EmpIdAndLeaveType_LeaveTypeIdAndYear(
            Integer empId, Integer leaveTypeId, Integer year);

    // Find all balances for a specific year
    List<LeaveBalance> findAllByEmployee_EmpIdAndYear(Integer empId, Integer year);


    // --- Server Changes (New Analytics/Dashboard Query) ---

    @Query("""
        SELECT COUNT(DISTINCT lb.employee.empId)
        FROM LeaveBalance lb
        WHERE lb.currentBalanceDays > 0
    """)
    long countEmployeesWithRemainingLeave();
}