package np.edu.nast.payroll.Payroll.controller;

import np.edu.nast.payroll.Payroll.entity.LeaveBalance;
// Ensure this import is correct to resolve the service symbol
import np.edu.nast.payroll.Payroll.service.LeaveBalanceService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/leave-balance")
@CrossOrigin(origins = "http://localhost:5173")
public class LeaveBalanceController {

    private final LeaveBalanceService leaveBalanceService;

    // FIX: Use the Service Interface as the parameter type, not the Entity
    public LeaveBalanceController(LeaveBalanceService service) {
        this.leaveBalanceService = service;
    }

    @GetMapping("/employee/{empId}")
    public List<LeaveBalance> getByEmployee(@PathVariable Integer empId) {
        // Correctly calls the service method using Integer empId
        return leaveBalanceService.getLeaveBalanceByEmployee(empId);
    }

    @PostMapping
    public LeaveBalance create(@RequestBody LeaveBalance b) {
        // Logic inside the service will prevent 'year' null errors
        return leaveBalanceService.createLeaveBalance(b);
    }

    @GetMapping
    public List<LeaveBalance> getAll() {
        return leaveBalanceService.getAllLeaveBalances();
    }
}