package np.edu.nast.payroll.Payroll.service;

import np.edu.nast.payroll.Payroll.entity.LeaveBalance;
import java.util.List;

public interface LeaveBalanceService {
<<<<<<< HEAD
    List<LeaveBalance> getLeaveBalanceByEmployee(Integer empId);
    LeaveBalance createLeaveBalance(LeaveBalance b);
    List<LeaveBalance> getAllLeaveBalances();
    void deleteLeaveBalance(Integer id);
    LeaveBalance getLeaveBalanceById(Integer id);

    // Updates totals for Admin Dashboard visibility
    void updateBalanceAfterApproval(Integer empId, Integer leaveTypeId, Integer days, Integer year);
}
=======

    LeaveBalance createLeaveBalance(LeaveBalance balance);
    LeaveBalance updateLeaveBalance(Long id, LeaveBalance balance);
    void deleteLeaveBalance(Long id);
    LeaveBalance getLeaveBalanceById(Long id);
    List<LeaveBalance> getAllLeaveBalances();
    List<LeaveBalance> getLeaveBalanceByEmployee(Long empId);
}
>>>>>>> 3214be41b790e5d207ff8a4a5185d56a25676df5
