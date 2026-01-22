package np.edu.nast.payroll.Payroll.controller;

import np.edu.nast.payroll.Payroll.entity.LeaveBalance;
import np.edu.nast.payroll.Payroll.service.LeaveBalanceService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leave-balance")
@CrossOrigin(origins = "http://localhost:5173")
public class LeaveBalanceController {

    private final LeaveBalanceService leaveBalanceService;

    // Standard constructor injection using the Service interface
    public LeaveBalanceController(LeaveBalanceService service) {
        this.leaveBalanceService = service;
    }

    @PostMapping
    public LeaveBalance create(@RequestBody LeaveBalance balance) {
        // Year and default balance logic is handled within the service layer
        return leaveBalanceService.createLeaveBalance(balance);
    }

    @PutMapping("/{id}")
    public LeaveBalance update(@PathVariable Integer id, @RequestBody LeaveBalance balance) {
        return leaveBalanceService.updateLeaveBalance(id, balance);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        leaveBalanceService.deleteLeaveBalance(id);
    }

    @GetMapping("/{id}")
    public LeaveBalance getById(@PathVariable Integer id) {
        return leaveBalanceService.getLeaveBalanceById(id);
    }

    @GetMapping
    public List<LeaveBalance> getAll() {
        return leaveBalanceService.getAllLeaveBalances();
    }

    @GetMapping("/employee/{empId}")
    public List<LeaveBalance> getByEmployee(@PathVariable Integer empId) {
        // PathVariable changed to Integer to match the Employee entity's empId
        return leaveBalanceService.getLeaveBalanceByEmployee(empId);
    }
}