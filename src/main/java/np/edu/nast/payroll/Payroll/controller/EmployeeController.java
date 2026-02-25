package np.edu.nast.payroll.Payroll.controller;

import np.edu.nast.payroll.Payroll.entity.Employee;
import np.edu.nast.payroll.Payroll.service.EmployeeService;
import np.edu.nast.payroll.Payroll.service.LeaveBalanceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile; // Added for photos

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService svc;
    private final LeaveBalanceService leaveBalanceService;

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
        leaveBalanceService.initializeBalancesForEmployee(Long.valueOf(savedEmployee.getEmpId()));
        return savedEmployee;
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/sync-balances")
    public ResponseEntity<String> syncAllBalances() {
        List<Employee> allEmployees = svc.getAll();
        for (Employee emp : allEmployees) {
            leaveBalanceService.initializeBalancesForEmployee(Long.valueOf(emp.getEmpId()));
        }
        return ResponseEntity.ok("Leave balances initialized for " + allEmployees.size() + " employees.");
    }

    // --- NEW SETTINGS FEATURES START HERE ---

    /**
     * Updates the employee's profile photo.
     * Matches frontend: POST /api/employees/{id}/upload-photo
     */
    @PostMapping("/{id}/upload-photo")
    public ResponseEntity<Map<String, String>> uploadPhoto(
            @PathVariable Integer id,
            @RequestParam("photo") MultipartFile file) {
        try {
            String photoUrl = svc.updateProfilePhoto(id, file);
            return ResponseEntity.ok(Map.of("photoUrl", photoUrl, "message", "Photo updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to upload photo: " + e.getMessage()));
        }
    }

    /**
     * Updates email notification preferences.
     * Matches frontend: PUT /api/employees/email-preference/{id}
     */
    @PutMapping("/email-preference/{id}")
    public ResponseEntity<String> updateEmailPreference(
            @PathVariable Integer id,
            @RequestBody Map<String, Boolean> payload) {
        Boolean preference = payload.get("emailNotifications");
        svc.updateEmailPreference(id, preference);
        return ResponseEntity.ok("Preference updated");
    }

    /**
     * Handles password changes with current password validation.
     * Matches frontend: PUT /api/employees/change-password/{id}
     */
    @PutMapping("/change-password/{id}")
    public ResponseEntity<Map<String, String>> changePassword(
            @PathVariable Integer id,
            @RequestBody Map<String, String> payload) {
        String currentPassword = payload.get("currentPassword");
        String newPassword = payload.get("newPassword");

        try {
            svc.updatePassword(id, currentPassword, newPassword);
            return ResponseEntity.ok(Map.of("message", "Password updated successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    // --- NEW SETTINGS FEATURES END HERE ---

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