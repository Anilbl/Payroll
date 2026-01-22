package np.edu.nast.payroll.Payroll.controller;

import np.edu.nast.payroll.Payroll.entity.LeaveBalance;
<<<<<<< HEAD
// Ensure this import is correct to resolve the service symbol
import np.edu.nast.payroll.Payroll.service.LeaveBalanceService;
import org.springframework.web.bind.annotation.*;
=======
import np.edu.nast.payroll.Payroll.service.LeaveBalanceService;
import org.springframework.web.bind.annotation.*;

>>>>>>> 3214be41b790e5d207ff8a4a5185d56a25676df5
import java.util.List;

@RestController
@RequestMapping("/api/leave-balance")
@CrossOrigin(origins = "http://localhost:5173")
public class LeaveBalanceController {

    private final LeaveBalanceService leaveBalanceService;

<<<<<<< HEAD
    // FIX: Use the Service Interface as the parameter type, not the Entity
=======
>>>>>>> 3214be41b790e5d207ff8a4a5185d56a25676df5
    public LeaveBalanceController(LeaveBalanceService service) {
        this.leaveBalanceService = service;
    }

<<<<<<< HEAD
    @GetMapping("/employee/{empId}")
    public List<LeaveBalance> getByEmployee(@PathVariable Integer empId) {
        // Correctly calls the service method using Integer empId
        return leaveBalanceService.getLeaveBalanceByEmployee(empId);
    }

    @PostMapping
    public LeaveBalance create(@RequestBody LeaveBalance b) {
        // Logic inside the service will prevent 'year' null errors
        return leaveBalanceService.createLeaveBalance(b);
=======
    @PostMapping
    public LeaveBalance create(@RequestBody LeaveBalance balance) {
        return leaveBalanceService.createLeaveBalance(balance);
    }

    @PutMapping("/{id}")
    public LeaveBalance update(@PathVariable Long id, @RequestBody LeaveBalance balance) {
        return leaveBalanceService.updateLeaveBalance(id, balance);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        leaveBalanceService.deleteLeaveBalance(id);
    }

    @GetMapping("/{id}")
    public LeaveBalance getById(@PathVariable Long id) {
        return leaveBalanceService.getLeaveBalanceById(id);
>>>>>>> 3214be41b790e5d207ff8a4a5185d56a25676df5
    }

    @GetMapping
    public List<LeaveBalance> getAll() {
        return leaveBalanceService.getAllLeaveBalances();
    }
<<<<<<< HEAD
}
=======

    @GetMapping("/employee/{empId}")
    public List<LeaveBalance> getByEmployee(@PathVariable Long empId) {
        return leaveBalanceService.getLeaveBalanceByEmployee(empId);
    }
}
>>>>>>> 3214be41b790e5d207ff8a4a5185d56a25676df5
