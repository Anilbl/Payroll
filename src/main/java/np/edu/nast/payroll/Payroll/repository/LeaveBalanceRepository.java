package np.edu.nast.payroll.Payroll.repository;
<<<<<<< HEAD

import np.edu.nast.payroll.Payroll.entity.LeaveBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface LeaveBalanceRepository extends JpaRepository<LeaveBalance, Integer> {

    // Fixes getLeaveBalanceByEmployee
    List<LeaveBalance> findByEmployee_EmpId(Integer empId);

    // Fixes updateBalanceAfterApproval
    Optional<LeaveBalance> findByEmployee_EmpIdAndLeaveType_LeaveTypeIdAndYear(
            Integer empId, Integer leaveTypeId, Integer year);

    // Fixes DashboardController error
    List<LeaveBalance> findAllByEmployee_EmpIdAndYear(Integer empId, Integer year);
}
=======
import np.edu.nast.payroll.Payroll.entity.LeaveBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LeaveBalanceRepository extends JpaRepository<LeaveBalance, Long> {
    List<LeaveBalance> findByEmployeeEmpId(Long empId);

    @Query("""
        SELECT COUNT(DISTINCT lb.employee.id)
        FROM LeaveBalance lb
        WHERE lb.currentBalanceDays > 0
    """)
    long countByCurrentBalanceDaysGreaterThan(double days);

}
>>>>>>> 3214be41b790e5d207ff8a4a5185d56a25676df5
