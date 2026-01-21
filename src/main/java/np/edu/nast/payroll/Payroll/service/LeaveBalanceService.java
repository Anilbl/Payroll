package np.edu.nast.payroll.Payroll.service;

import np.edu.nast.payroll.Payroll.entity.LeaveBalance;
import java.util.List;

public interface LeaveBalanceService {
    List<LeaveBalance> getLeaveBalanceByEmployee(Integer empId);
    LeaveBalance createLeaveBalance(LeaveBalance b);
    List<LeaveBalance> getAllLeaveBalances();
    void deleteLeaveBalance(Integer id);
    LeaveBalance getLeaveBalanceById(Integer id);

    // Updates totals for Admin Dashboard visibility
    void updateBalanceAfterApproval(Integer empId, Integer leaveTypeId, Integer days, Integer year);
}