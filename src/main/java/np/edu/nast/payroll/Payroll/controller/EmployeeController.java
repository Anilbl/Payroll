package np.edu.nast.payroll.Payroll.controller;

import np.edu.nast.payroll.Payroll.entity.Employee;
import np.edu.nast.payroll.Payroll.service.EmployeeService;
import np.edu.nast.payroll.Payroll.service.LeaveBalanceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService svc;
    private final LeaveBalanceService leaveBalanceService; // Added

    public EmployeeController(EmployeeService svc, LeaveBalanceService leaveBalanceService) {
        this.svc = svc;
        this.leaveBalanceService = leaveBalanceService;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Employee> getByUserId(@PathVariable("userId") Integer userId) {
        Employee employee = svc.getByUserId(userId);
        return ResponseEntity.ok(employee);
    }

    @GetMapping("/dashboard/stats/{id}")
    public Map<String, Object> getDashboardStats(@PathVariable Integer id) {
        return svc.getDashboardStats(id);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_ACCOUNTANT', 'ADMIN', 'ACCOUNTANT')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Employee create(@RequestBody Employee employee) {
        Employee savedEmployee = svc.create(employee);

        // ✅ CRITICAL: Automatically initialize leave balances (quota) for the new employee
        leaveBalanceService.initializeBalancesForEmployee(Long.valueOf(savedEmployee.getEmpId()));

        return savedEmployee;
    }

    /**
     * ✅ ONE-TIME SYNC: Run this once via Postman/Browser to fill your currently empty
     * leave_balance table for all existing employees.
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/sync-balances")
    public ResponseEntity<String> syncAllBalances() {
        List<Employee> allEmployees = svc.getAll();
        for (Employee emp : allEmployees) {
            leaveBalanceService.initializeBalancesForEmployee(Long.valueOf(emp.getEmpId()));
        }
        return ResponseEntity.ok("Leave balances initialized for " + allEmployees.size() + " employees.");
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_ACCOUNTANT', 'ADMIN', 'ACCOUNTANT')")
    @GetMapping
    public List<Employee> getAll() {
        return svc.getAll();
    }

    @GetMapping("/{id}")
    public Employee getById(@PathVariable Integer id) {
        return svc.getById(id);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_ACCOUNTANT', 'ADMIN', 'ACCOUNTANT')")
    @PutMapping("/{id}")
    public Employee update(@PathVariable Integer id, @RequestBody Employee employee) {
        return svc.update(id, employee);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_ACCOUNTANT', 'ADMIN', 'ACCOUNTANT')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        svc.delete(id);
    }

    @GetMapping("/email/{email:.+}")
    public Employee getByEmail(@PathVariable String email) {
        return svc.getByEmail(email);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_ACCOUNTANT', 'ADMIN', 'ACCOUNTANT')")
    @GetMapping("/stats/active-per-month")
    public Map<Integer, Long> getActiveEmployeeStats() {
        return svc.getActiveEmployeeStats();
    }
}