package np.edu.nast.payroll.Payroll.controller;

import lombok.RequiredArgsConstructor;
import np.edu.nast.payroll.Payroll.entity.EmployeeLeave;
import np.edu.nast.payroll.Payroll.service.EmployeeLeaveService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/employee-leaves")
@CrossOrigin(origins = "http://localhost:5173", allowedHeaders = "*")
@RequiredArgsConstructor
public class EmployeeLeaveController {

    private final EmployeeLeaveService employeeLeaveService;

    /* =========================
       LEAVE STATUS UPDATE
       ========================= */
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(
            @PathVariable Integer id,
            @RequestBody Map<String, Object> payload) {
        try {
            String status = (String) payload.get("status");

            // Extract adminId from JSON; default to 1 if not provided
            Object adminIdObj = payload.get("adminId");
            Integer adminId = (adminIdObj != null) ? Integer.parseInt(adminIdObj.toString()) : 1;

            EmployeeLeave updatedLeave = employeeLeaveService.updateLeaveStatus(id, status, adminId);
            return ResponseEntity.ok(updatedLeave);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /* =========================
       REQUEST NEW LEAVE
       ========================= */
    @PostMapping
    public ResponseEntity<EmployeeLeave> requestLeave(@RequestBody EmployeeLeave leave) {
        return ResponseEntity.ok(employeeLeaveService.requestLeave(leave));
    }

    /* =========================
       GET ALL LEAVE REQUESTS
       ========================= */
    @GetMapping
    public ResponseEntity<List<EmployeeLeave>> getAll() {
        return ResponseEntity.ok(employeeLeaveService.getAllLeaves());
    }
}