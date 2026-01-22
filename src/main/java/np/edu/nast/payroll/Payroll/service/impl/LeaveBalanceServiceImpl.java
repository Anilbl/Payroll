package np.edu.nast.payroll.Payroll.service.impl;

import lombok.RequiredArgsConstructor;
import np.edu.nast.payroll.Payroll.entity.LeaveBalance;
import np.edu.nast.payroll.Payroll.repository.LeaveBalanceRepository;
import np.edu.nast.payroll.Payroll.service.LeaveBalanceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LeaveBalanceServiceImpl implements LeaveBalanceService {

    private final LeaveBalanceRepository leaveBalanceRepository;

    @Override
    public List<LeaveBalance> getLeaveBalanceByEmployee(Integer empId) {
        // Standardized to Integer ID to match Employee entity
        return leaveBalanceRepository.findByEmployee_EmpId(empId);
    }

    @Override
    public LeaveBalance createLeaveBalance(LeaveBalance b) {
        // Automatically set the current year if not provided
        if (b.getYear() == null) {
            b.setYear(LocalDate.now().getYear());
        }
        return leaveBalanceRepository.save(b);
    }

    @Override
    @Transactional
    public LeaveBalance updateLeaveBalance(Integer id, LeaveBalance balance) {
        LeaveBalance existing = leaveBalanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Leave balance record not found with ID: " + id));

        existing.setCurrentBalanceDays(balance.getCurrentBalanceDays());
        existing.setLeaveTaken(balance.getLeaveTaken());
        existing.setYear(balance.getYear());

        return leaveBalanceRepository.save(existing);
    }

    @Override
    @Transactional
    public void updateBalanceAfterApproval(Integer empId, Integer leaveTypeId, Integer days, Integer year) {
        // Crucial logic: Deducts days from the balance when a leave request is approved
        LeaveBalance balance = leaveBalanceRepository
                .findByEmployee_EmpIdAndLeaveType_LeaveTypeIdAndYear(empId, leaveTypeId, year)
                .orElse(null);

        if (balance != null) {
            double current = balance.getCurrentBalanceDays() != null ? balance.getCurrentBalanceDays() : 0.0;
            double taken = balance.getLeaveTaken() != null ? balance.getLeaveTaken() : 0.0;

            balance.setCurrentBalanceDays(Math.max(0, current - days.doubleValue()));
            balance.setLeaveTaken(taken + days.doubleValue());
            leaveBalanceRepository.save(balance);
        }
    }

    @Override
    public List<LeaveBalance> getAllLeaveBalances() {
        return leaveBalanceRepository.findAll();
    }

    @Override
    public void deleteLeaveBalance(Integer id) {
        if (!leaveBalanceRepository.existsById(id)) {
            throw new RuntimeException("Cannot delete. Leave balance not found with ID: " + id);
        }
        leaveBalanceRepository.deleteById(id);
    }

    @Override
    public LeaveBalance getLeaveBalanceById(Integer id) {
        return leaveBalanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Leave balance not found with ID: " + id));
    }
}