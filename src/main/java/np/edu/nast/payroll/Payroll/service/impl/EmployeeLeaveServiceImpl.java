package np.edu.nast.payroll.Payroll.service.impl;

import lombok.RequiredArgsConstructor;
<<<<<<< HEAD
import np.edu.nast.payroll.Payroll.entity.*;
import np.edu.nast.payroll.Payroll.repository.*;
=======
import np.edu.nast.payroll.Payroll.entity.Employee;
import np.edu.nast.payroll.Payroll.entity.EmployeeLeave;
import np.edu.nast.payroll.Payroll.entity.LeaveType;
import np.edu.nast.payroll.Payroll.entity.User;
import np.edu.nast.payroll.Payroll.repository.EmployeeLeaveRepository;
import np.edu.nast.payroll.Payroll.repository.EmployeeRepository;
import np.edu.nast.payroll.Payroll.repository.LeaveTypeRepository;
import np.edu.nast.payroll.Payroll.repository.UserRepository;
>>>>>>> 3214be41b790e5d207ff8a4a5185d56a25676df5
import np.edu.nast.payroll.Payroll.service.EmployeeLeaveService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

<<<<<<< HEAD
import java.time.LocalDate;
=======
>>>>>>> 3214be41b790e5d207ff8a4a5185d56a25676df5
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeLeaveServiceImpl implements EmployeeLeaveService {

    private final EmployeeLeaveRepository employeeLeaveRepo;
    private final UserRepository userRepo;
    private final EmployeeRepository employeeRepo;
    private final LeaveTypeRepository leaveTypeRepo;
<<<<<<< HEAD
    private final LeaveBalanceRepository leaveBalanceRepo;
=======

    @Override
    public List<EmployeeLeave> getAllLeaves() {
        return employeeLeaveRepo.findAll();
    }
>>>>>>> 3214be41b790e5d207ff8a4a5185d56a25676df5

    @Override
    @Transactional
    public EmployeeLeave updateLeaveStatus(Integer id, String status, Integer adminId) {
<<<<<<< HEAD
        // 1. Fetch the Leave Request
        EmployeeLeave leave = employeeLeaveRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Leave record not found with ID: " + id));

        int currentYear = LocalDate.now().getYear();

        // 2. Business Logic: Only deduct balance if transitioning to "Approved"
        if ("Approved".equalsIgnoreCase(status) && !"Approved".equalsIgnoreCase(leave.getStatus())) {

            // Fetch or Initialize Leave Balance for the current year
            LeaveBalance balance = leaveBalanceRepo.findByEmployee_EmpIdAndLeaveType_LeaveTypeIdAndYear(
                    leave.getEmployee().getEmpId(),
                    leave.getLeaveType().getLeaveTypeId(),
                    currentYear
            ).orElseGet(() -> {
                LeaveBalance newBalance = new LeaveBalance();
                newBalance.setEmployee(leave.getEmployee());
                newBalance.setLeaveType(leave.getLeaveType());
                newBalance.setCurrentBalanceDays(15.0); // Default allowance
                newBalance.setLeaveTaken(0.0);
                newBalance.setYear(currentYear);
                return leaveBalanceRepo.save(newBalance);
            });

            // Handle potential nulls and perform deduction
            double current = (balance.getCurrentBalanceDays() != null) ? balance.getCurrentBalanceDays() : 0.0;
            double taken = (balance.getLeaveTaken() != null) ? balance.getLeaveTaken() : 0.0;
            double requested = (leave.getTotalDays() != null) ? leave.getTotalDays().doubleValue() : 0.0;

            if (current < requested) {
                throw new RuntimeException("Insufficient leave balance! Available: " + current);
            }

            // Update Balance
            balance.setCurrentBalanceDays(current - requested);
            balance.setLeaveTaken(taken + requested);
            leaveBalanceRepo.save(balance);

            // Audit: Set Approver details
            User admin = userRepo.findById(adminId)
                    .orElseThrow(() -> new IllegalArgumentException("Admin User not found: " + adminId));
            leave.setApprovedBy(admin);
            leave.setApprovedAt(LocalDateTime.now());

        } else if ("Rejected".equalsIgnoreCase(status)) {
=======
        EmployeeLeave leave = employeeLeaveRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Leave record not found with ID: " + id));

        leave.setStatus(status);

        if ("Approved".equalsIgnoreCase(status)) {
            User admin = userRepo.findById(adminId)
                    .orElseThrow(() -> new IllegalArgumentException("Admin User not found with ID: " + adminId));
            leave.setApprovedBy(admin);
            leave.setApprovedAt(LocalDateTime.now());
        } else {
>>>>>>> 3214be41b790e5d207ff8a4a5185d56a25676df5
            leave.setApprovedBy(null);
            leave.setApprovedAt(null);
        }

<<<<<<< HEAD
        leave.setStatus(status);
=======
>>>>>>> 3214be41b790e5d207ff8a4a5185d56a25676df5
        return employeeLeaveRepo.save(leave);
    }

    @Override
    @Transactional
    public EmployeeLeave requestLeave(EmployeeLeave leave) {
        // Validate Employee
        if (leave.getEmployee() == null || leave.getEmployee().getEmpId() == null) {
            throw new IllegalArgumentException("Employee ID is required");
        }
        Employee employee = employeeRepo.findById(leave.getEmployee().getEmpId())
                .orElseThrow(() -> new IllegalArgumentException("Employee not found"));
        leave.setEmployee(employee);

<<<<<<< HEAD
        // Validate Leave Type
=======
        // Validate LeaveType
>>>>>>> 3214be41b790e5d207ff8a4a5185d56a25676df5
        if (leave.getLeaveType() == null || leave.getLeaveType().getLeaveTypeId() == null) {
            throw new IllegalArgumentException("Leave Type ID is required");
        }
        LeaveType leaveType = leaveTypeRepo.findById(leave.getLeaveType().getLeaveTypeId())
                .orElseThrow(() -> new IllegalArgumentException("Leave Type not found"));
        leave.setLeaveType(leaveType);

<<<<<<< HEAD
        calculateAndSetTotalDays(leave);

=======
        // Calculate Total Days
        calculateAndSetTotalDays(leave);

        // Set Default Status
>>>>>>> 3214be41b790e5d207ff8a4a5185d56a25676df5
        if (leave.getStatus() == null || leave.getStatus().isEmpty()) {
            leave.setStatus("Pending");
        }

<<<<<<< HEAD
=======
        // Validate ApprovedBy if provided
        if (leave.getApprovedBy() != null && leave.getApprovedBy().getUserId() != null) {
            User approvedBy = userRepo.findById(leave.getApprovedBy().getUserId())
                    .orElseThrow(() -> new IllegalArgumentException("Approved By User not found with ID: " + leave.getApprovedBy().getUserId()));
            leave.setApprovedBy(approvedBy);
        }

>>>>>>> 3214be41b790e5d207ff8a4a5185d56a25676df5
        return employeeLeaveRepo.save(leave);
    }

    @Override
<<<<<<< HEAD
    public List<EmployeeLeave> getLeavesByEmployee(Integer empId) {
        return employeeLeaveRepo.findAllByEmployee_EmpId(empId);
    }

    @Override public List<EmployeeLeave> getAllLeaves() { return employeeLeaveRepo.findAll(); }

    @Override public EmployeeLeave getLeaveById(Integer id) { return employeeLeaveRepo.findById(id).orElse(null); }

    @Override public void deleteLeave(Integer id) {
        EmployeeLeave leave = employeeLeaveRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Leave not found"));
=======
    public EmployeeLeave getLeaveById(Integer id) {
        return employeeLeaveRepo.findById(id).orElse(null);
    }

    @Override
    public List<EmployeeLeave> getLeavesByEmployee(Integer empId) {
        Employee employee = employeeRepo.findById(empId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found with ID: " + empId));
        return employeeLeaveRepo.findAllByEmployee(employee);
    }

    @Override
    public void deleteLeave(Integer id) {
        EmployeeLeave leave = employeeLeaveRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Leave not found with ID: " + id));
>>>>>>> 3214be41b790e5d207ff8a4a5185d56a25676df5
        employeeLeaveRepo.delete(leave);
    }

    @Override
    @Transactional
    public EmployeeLeave updateLeave(Integer id, EmployeeLeave leave) {
<<<<<<< HEAD
        EmployeeLeave existing = employeeLeaveRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Leave not found"));

        existing.setStartDate(leave.getStartDate());
        existing.setEndDate(leave.getEndDate());
        calculateAndSetTotalDays(existing);
        existing.setReason(leave.getReason());
        existing.setStatus(leave.getStatus());

        return employeeLeaveRepo.save(existing);
    }

    private void calculateAndSetTotalDays(EmployeeLeave leave) {
        if (leave.getStartDate() != null && leave.getEndDate() != null) {
            long days = ChronoUnit.DAYS.between(leave.getStartDate(), leave.getEndDate()) + 1;
            if (days <= 0) throw new IllegalArgumentException("End date must be after start date");
            leave.setTotalDays((int) days);
        }
    }
}
=======
        EmployeeLeave existingLeave = employeeLeaveRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Leave not found with ID: " + id));

        // Update Dates and Recalculate Total Days
        existingLeave.setStartDate(leave.getStartDate());
        existingLeave.setEndDate(leave.getEndDate());
        calculateAndSetTotalDays(existingLeave);

        // Update Employee
        if (leave.getEmployee() != null && leave.getEmployee().getEmpId() != null) {
            Employee employee = employeeRepo.findById(leave.getEmployee().getEmpId())
                    .orElseThrow(() -> new IllegalArgumentException("Employee not found"));
            existingLeave.setEmployee(employee);
        }

        // Update LeaveType
        if (leave.getLeaveType() != null && leave.getLeaveType().getLeaveTypeId() != null) {
            LeaveType leaveType = leaveTypeRepo.findById(leave.getLeaveType().getLeaveTypeId())
                    .orElseThrow(() -> new IllegalArgumentException("Leave Type not found"));
            existingLeave.setLeaveType(leaveType);
        }

        // Update other fields
        existingLeave.setReason(leave.getReason());
        existingLeave.setStatus(leave.getStatus());

        // Update ApprovedBy if provided
        if (leave.getApprovedBy() != null && leave.getApprovedBy().getUserId() != null) {
            User approvedBy = userRepo.findById(leave.getApprovedBy().getUserId())
                    .orElseThrow(() -> new IllegalArgumentException("Approved By User not found with ID: " + leave.getApprovedBy().getUserId()));
            existingLeave.setApprovedBy(approvedBy);
        }

        return employeeLeaveRepo.save(existingLeave);
    }

    /**
     * Helper method to calculate duration between start and end date
     */
    private void calculateAndSetTotalDays(EmployeeLeave leave) {
        if (leave.getStartDate() != null && leave.getEndDate() != null) {
            long days = ChronoUnit.DAYS.between(leave.getStartDate(), leave.getEndDate()) + 1;
            if (days <= 0) {
                throw new IllegalArgumentException("End date must be after start date");
            }
            leave.setTotalDays((int) days);
        }
    }
}
>>>>>>> 3214be41b790e5d207ff8a4a5185d56a25676df5
