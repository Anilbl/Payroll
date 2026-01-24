package np.edu.nast.payroll.Payroll.service;

import np.edu.nast.payroll.Payroll.entity.LeaveBalance;
import java.util.List;

public interface LeaveBalanceService {

    // --- KEEPING ORIGINAL SIGNATURES (LONG) ---
    LeaveBalance createLeaveBalance(LeaveBalance balance);

    LeaveBalance updateLeaveBalance(Long id, LeaveBalance balance);

    void deleteLeaveBalance(Long id);

    LeaveBalance getLeaveBalanceById(Long id);

    List<LeaveBalance> getAllLeaveBalances();

    List<LeaveBalance> getLeaveBalanceByEmployee(Long empId);

    // --- NEW LOGIC METHODS (ADAPTED TO LONG) ---

    /**
     * ✅ Initialization Logic:
     * This will fill your empty leave_balance table for an employee
     * using the default days from LeaveType.
     */
    void initializeBalancesForEmployee(Long empId);

    /**
     * ✅ Balance Deduction/Refund:
     * Triggered when a leave is Approved (deduct) or Rejected after approval (refund).
     */
    void updateBalanceOnStatusChange(Integer leaveId, boolean isDeducting);
}