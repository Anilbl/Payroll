package np.edu.nast.payroll.Payroll.service;

import np.edu.nast.payroll.Payroll.entity.LeaveBalance;
import java.util.List;

public interface LeaveBalanceService {

    // Standard CRUD Operations
    LeaveBalance createLeaveBalance(LeaveBalance balance);

    LeaveBalance getLeaveBalanceById(Integer id);

    List<LeaveBalance> getAllLeaveBalances();

    List<LeaveBalance> getLeaveBalanceByEmployee(Integer empId);

    LeaveBalance updateLeaveBalance(Integer id, LeaveBalance balance);

    void deleteLeaveBalance(Integer id);

    /**
     * Updates totals for Admin Dashboard visibility and deducts
     * leave days from the employee's balance after a leave request is approved.
     */
    void updateBalanceAfterApproval(Integer empId, Integer leaveTypeId, Integer days, Integer year);
}