package np.edu.nast.payroll.Payroll.service.impl;

import lombok.RequiredArgsConstructor;
import np.edu.nast.payroll.Payroll.entity.*;
import np.edu.nast.payroll.Payroll.repository.*;
import np.edu.nast.payroll.Payroll.service.EmployeeLeaveService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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
    private final LeaveBalanceRepository leaveBalanceRepo;

    @Override
    @Transactional
    public EmployeeLeave updateLeaveStatus(Integer id, String status, Integer adminId) {
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
            leave.setApprovedBy(null);
            leave.setApprovedAt(null);
        }

        leave.setStatus(status);
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

        // Validate Leave Type
        if (leave.getLeaveType() == null || leave.getLeaveType().getLeaveTypeId() == null) {
            throw new IllegalArgumentException("Leave Type ID is required");
        }
        LeaveType leaveType = leaveTypeRepo.findById(leave.getLeaveType().getLeaveTypeId())
                .orElseThrow(() -> new IllegalArgumentException("Leave Type not found"));
        leave.setLeaveType(leaveType);

        calculateAndSetTotalDays(leave);

        if (leave.getStatus() == null || leave.getStatus().isEmpty()) {
            leave.setStatus("Pending");
        }

        return employeeLeaveRepo.save(leave);
    }

    @Override
    public List<EmployeeLeave> getLeavesByEmployee(Integer empId) {
        return employeeLeaveRepo.findAllByEmployee_EmpId(empId);
    }

    @Override public List<EmployeeLeave> getAllLeaves() { return employeeLeaveRepo.findAll(); }

    @Override public EmployeeLeave getLeaveById(Integer id) { return employeeLeaveRepo.findById(id).orElse(null); }

    @Override public void deleteLeave(Integer id) {
        EmployeeLeave leave = employeeLeaveRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Leave not found"));
        employeeLeaveRepo.delete(leave);
    }

    @Override
    @Transactional
    public EmployeeLeave updateLeave(Integer id, EmployeeLeave leave) {
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