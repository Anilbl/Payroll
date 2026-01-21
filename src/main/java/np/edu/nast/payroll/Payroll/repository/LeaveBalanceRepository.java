package np.edu.nast.payroll.Payroll.repository;

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