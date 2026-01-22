package np.edu.nast.payroll.Payroll.service;

import np.edu.nast.payroll.Payroll.entity.EmployeeLeave;
import java.util.List;

public interface EmployeeLeaveService {

    /**
     * Submits a new leave request for an employee
     */
    EmployeeLeave requestLeave(EmployeeLeave leave);

    /**
     * Retrieves all leave records in the system
     */
    List<EmployeeLeave> getAllLeaves();

    /**
     * Retrieves a specific leave record by its ID
     */
    EmployeeLeave getLeaveById(Integer id);

    /**
     * Retrieves all leave records belonging to a specific employee
     */
    List<EmployeeLeave> getLeavesByEmployee(Integer empId);

    /**
     * Removes a leave record from the system
     */
    void deleteLeave(Integer id);

    /**
     * Updates details of an existing leave (dates, reason, etc.)
     */
    EmployeeLeave updateLeave(Integer id, EmployeeLeave leave);

    /**
     * Updates only the status (Approved/Rejected) and captures admin audit details
     * * @param id The ID of the leave record
     * @param status The new status ("Approved", "Rejected", etc.)
     * @param adminId The ID of the User performing the action
     * @return The updated EmployeeLeave object
     */
    EmployeeLeave updateLeaveStatus(Integer id, String status, Integer adminId);
}