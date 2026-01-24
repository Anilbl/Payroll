package np.edu.nast.payroll.Payroll.service.impl;

import lombok.RequiredArgsConstructor;
import np.edu.nast.payroll.Payroll.entity.Employee;
import np.edu.nast.payroll.Payroll.entity.LeaveBalance;
import np.edu.nast.payroll.Payroll.entity.LeaveType;
import np.edu.nast.payroll.Payroll.repository.EmployeeRepository;
import np.edu.nast.payroll.Payroll.repository.LeaveBalanceRepository;
import np.edu.nast.payroll.Payroll.repository.LeaveTypeRepository;
import np.edu.nast.payroll.Payroll.service.LeaveBalanceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LeaveBalanceServiceImpl implements LeaveBalanceService {

    private final LeaveBalanceRepository leaveBalanceRepository;
    private final EmployeeRepository employeeRepo;
    private final LeaveTypeRepository leaveTypeRepo;

    @Override
    public LeaveBalance createLeaveBalance(LeaveBalance balance) {
        return leaveBalanceRepository.save(balance);
    }

    @Override
    public LeaveBalance updateLeaveBalance(Long id, LeaveBalance balance) {
        // Fix: Cast Long to Integer to match the Repository's findById(Integer)
        LeaveBalance existing = leaveBalanceRepository.findById(id.intValue())
                .orElseThrow(() -> new RuntimeException("Leave balance not found with ID: " + id));

        existing.setCurrentBalanceDays(balance.getCurrentBalanceDays());
        existing.setYear(balance.getYear());

        return leaveBalanceRepository.save(existing);
    }

    @Override
    public void deleteLeaveBalance(Long id) {
        // Fix: Cast Long to Integer
        leaveBalanceRepository.deleteById(id.intValue());
    }

    @Override
    public LeaveBalance getLeaveBalanceById(Long id) {
        // Fix: Cast Long to Integer
        return leaveBalanceRepository.findById(id.intValue())
                .orElseThrow(() -> new RuntimeException("Leave balance not found"));
    }

    @Override
    public List<LeaveBalance> getAllLeaveBalances() {
        return leaveBalanceRepository.findAll();
    }

    @Override
    public List<LeaveBalance> getLeaveBalanceByEmployee(Long empId) {
        // Fix: Cast Long to Integer for the Repository custom query
        return leaveBalanceRepository.findByEmployeeEmpId(empId.intValue());
    }

    @Override
    @Transactional
    public void initializeBalancesForEmployee(Long empId) {
        Employee employee = employeeRepo.findById(empId.intValue())
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        List<LeaveType> allTypes = leaveTypeRepo.findAll();
        int currentYear = LocalDate.now().getYear();

        for (LeaveType type : allTypes) {
            boolean exists = leaveBalanceRepository
                    .findByEmployeeAndLeaveTypeAndYear(employee, type, currentYear)
                    .isPresent();

            if (!exists) {
                LeaveBalance lb = LeaveBalance.builder()
                        .employee(employee)
                        .leaveType(type)
                        .currentBalanceDays((double) type.getDefaultDaysPerYear())
                        .year(currentYear)
                        .build();
                leaveBalanceRepository.save(lb);
            }
        }
    }

    /**
     * ✅ FIX: Implementing the missing abstract method to clear the IDE error.
     */
    @Override
    @Transactional
    public void updateBalanceOnStatusChange(Integer leaveId, boolean isDeducting) {
        // You can leave this empty if you are handling the logic in EmployeeLeaveServiceImpl,
        // but the method MUST exist here to satisfy the Interface.
    }
}