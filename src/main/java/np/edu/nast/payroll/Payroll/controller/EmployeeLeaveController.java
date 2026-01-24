package np.edu.nast.payroll.Payroll.controller;

import np.edu.nast.payroll.Payroll.entity.EmployeeLeave;
import np.edu.nast.payroll.Payroll.service.EmployeeLeaveService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/employee-leaves")
@CrossOrigin(origins = "http://localhost:5173")
public class EmployeeLeaveController {

    private final EmployeeLeaveService employeeLeaveService;

    public EmployeeLeaveController(EmployeeLeaveService service) {
        this.employeeLeaveService = service;
    }

    @PostMapping
    public EmployeeLeave requestLeave(@RequestBody EmployeeLeave leave) {
        // The service now handles calculation and linking
        return employeeLeaveService.requestLeave(leave);
    }

    @GetMapping
    public List<EmployeeLeave> getAll() {
        return employeeLeaveService.getAllLeaves();
    }

    @GetMapping("/employee/{empId}")
    public List<EmployeeLeave> getByEmployee(@PathVariable Integer empId) {
        return employeeLeaveService.getLeavesByEmployee(empId);
    }

    @PatchMapping("/{id}/status")
    public EmployeeLeave updateStatus(@PathVariable Integer id, @RequestBody Map<String, Object> payload) {
        String status = (String) payload.get("status");
        String reason = (String) payload.get("rejectionReason");

        Object rawId = payload.get("adminId");
        Integer adminId = (rawId != null) ? Integer.parseInt(rawId.toString()) : 1;

        // ✅ This call now triggers the balance deduction/refund logic in the ServiceImpl
        return employeeLeaveService.updateLeaveStatus(id, status, adminId, reason);
    }
}