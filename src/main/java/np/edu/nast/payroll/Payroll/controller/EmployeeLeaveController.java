package np.edu.nast.payroll.Payroll.controller;

<<<<<<< HEAD
import lombok.RequiredArgsConstructor;
import np.edu.nast.payroll.Payroll.entity.EmployeeLeave;
import np.edu.nast.payroll.Payroll.service.EmployeeLeaveService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/employee-leaves")
// Note: We keep @CrossOrigin, but your SecurityConfig will handle most of the heavy lifting.
@CrossOrigin(origins = "http://localhost:5173", allowedHeaders = "*")
@RequiredArgsConstructor
=======
import np.edu.nast.payroll.Payroll.entity.EmployeeLeave;
import np.edu.nast.payroll.Payroll.service.EmployeeLeaveService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/employee-leaves")
@CrossOrigin(origins = "http://localhost:5173")
>>>>>>> 3214be41b790e5d207ff8a4a5185d56a25676df5
public class EmployeeLeaveController {

    private final EmployeeLeaveService employeeLeaveService;

<<<<<<< HEAD
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(
            @PathVariable Integer id,
            @RequestParam String status,
            @RequestParam Integer adminId) {
        try {
            // Use the service to update status and get the updated entity
            EmployeeLeave updatedLeave = employeeLeaveService.updateLeaveStatus(id, status, adminId);
            return ResponseEntity.ok(updatedLeave);
        } catch (Exception e) {
            // If service throws an error (e.g., ID not found), return 400 with the error message
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<EmployeeLeave> requestLeave(@RequestBody EmployeeLeave leave) {
        return ResponseEntity.ok(employeeLeaveService.requestLeave(leave));
    }

    @GetMapping
    public ResponseEntity<List<EmployeeLeave>> getAll() {
        return ResponseEntity.ok(employeeLeaveService.getAllLeaves());
=======
    public EmployeeLeaveController(EmployeeLeaveService service) {
        this.employeeLeaveService = service;
    }

    @PostMapping
    public EmployeeLeave requestLeave(@RequestBody EmployeeLeave leave) {
        return employeeLeaveService.requestLeave(leave);
    }

    @GetMapping
    public List<EmployeeLeave> getAll() {
        return employeeLeaveService.getAllLeaves();
    }

    @PatchMapping("/{id}/status")
    public EmployeeLeave updateStatus(@PathVariable Integer id, @RequestBody Map<String, Object> statusUpdate) {
        String status = (String) statusUpdate.get("status");

        // Extract adminId from JSON and convert to Integer to match Service requirements
        Object adminIdObj = statusUpdate.get("adminId");
        Integer adminId = (adminIdObj != null) ? Integer.parseInt(adminIdObj.toString()) : 1;

        // Passed 3 arguments to satisfy the service interface
        return employeeLeaveService.updateLeaveStatus(id, status, adminId);
>>>>>>> 3214be41b790e5d207ff8a4a5185d56a25676df5
    }
}