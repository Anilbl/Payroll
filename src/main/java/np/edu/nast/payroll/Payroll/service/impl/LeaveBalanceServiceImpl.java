package np.edu.nast.payroll.Payroll.service.impl;

import np.edu.nast.payroll.Payroll.entity.LeaveBalance;
import np.edu.nast.payroll.Payroll.repository.LeaveBalanceRepository;
import np.edu.nast.payroll.Payroll.service.LeaveBalanceService;
<<<<<<< HEAD
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
=======
import org.springframework.stereotype.Service;

>>>>>>> 3214be41b790e5d207ff8a4a5185d56a25676df5
import java.util.List;

@Service
public class LeaveBalanceServiceImpl implements LeaveBalanceService {

<<<<<<< HEAD
    @Autowired
    private LeaveBalanceRepository leaveBalanceRepository;

    @Override
    public List<LeaveBalance> getLeaveBalanceByEmployee(Integer empId) {
        // Requires findByEmployee_EmpId in Repository
        return leaveBalanceRepository.findByEmployee_EmpId(empId);
    }

    @Override
    public LeaveBalance createLeaveBalance(LeaveBalance b) {
        if (b.getYear() == null) {
            b.setYear(LocalDate.now().getYear());
        }
        return leaveBalanceRepository.save(b);
    }

    @Override
    @Transactional
    public void updateBalanceAfterApproval(Integer empId, Integer leaveTypeId, Integer days, Integer year) {
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

    @Override public List<LeaveBalance> getAllLeaveBalances() { return leaveBalanceRepository.findAll(); }
    @Override public void deleteLeaveBalance(Integer id) { leaveBalanceRepository.deleteById(id); }
    @Override public LeaveBalance getLeaveBalanceById(Integer id) { return leaveBalanceRepository.findById(id).orElse(null); }
}
=======
    private final LeaveBalanceRepository leaveBalanceRepository;

    public LeaveBalanceServiceImpl(LeaveBalanceRepository repo) {
        this.leaveBalanceRepository = repo;
    }

    @Override
    public LeaveBalance createLeaveBalance(LeaveBalance balance) {
        return leaveBalanceRepository.save(balance);
    }

    @Override
    public LeaveBalance updateLeaveBalance(Long id, LeaveBalance balance) {
        LeaveBalance existing = leaveBalanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Leave balance not found"));

        existing.setCurrentBalanceDays(balance.getCurrentBalanceDays());
        existing.setYear(balance.getYear());

        return leaveBalanceRepository.save(existing);
    }

    @Override
    public void deleteLeaveBalance(Long id) {
        leaveBalanceRepository.deleteById(id);
    }

    @Override
    public LeaveBalance getLeaveBalanceById(Long id) {
        return leaveBalanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Leave balance not found"));
    }

    @Override
    public List<LeaveBalance> getAllLeaveBalances() {
        return leaveBalanceRepository.findAll();
    }

    @Override
    public List<LeaveBalance> getLeaveBalanceByEmployee(Long empId) {
        return leaveBalanceRepository.findByEmployeeEmpId(empId);
    }
}
>>>>>>> 3214be41b790e5d207ff8a4a5185d56a25676df5
