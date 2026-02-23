package np.edu.nast.payroll.Payroll.service;

import np.edu.nast.payroll.Payroll.entity.EmployeeLeave;
import java.util.List;

public interface EmployeeLeaveService {

    /**
     * Submits a new leave request.
     */
    EmployeeLeave requestLeave(EmployeeLeave leave);

    /**
     * Retrieves all leave records in the system.
     */
    List<EmployeeLeave> getAllLeaves();

    /**
     * NEW: Retrieves leaves based on year, month, status, and search criteria.
     * Supports the dynamic filtering on the Admin Leave page.
     */
    List<EmployeeLeave> getFilteredLeaves(Integer year, Integer month, String status, String search);

    /**
     * Retrieves a specific leave record by ID.
     */
    EmployeeLeave getLeaveById(Integer id);

    /**
     * Retrieves all leaves requested by a specific employee.
     */
    List<EmployeeLeave> getLeavesByEmployee(Integer empId);

    /**
     * Deletes a leave record.
     */
    void deleteLeave(Integer id);

    /**
     * Updates details of an existing leave record.
     */
    EmployeeLeave updateLeave(Integer id, EmployeeLeave leave);

    /**
     * Handles the Approval/Rejection logic, admin tracking, and balance deduction/refund.
     */
    EmployeeLeave updateLeaveStatus(Integer id, String status, Integer adminId, String rejectionReason);
}