package np.edu.nast.payroll.Payroll.controller;

import lombok.RequiredArgsConstructor;
import np.edu.nast.payroll.Payroll.entity.PayrollExtraComponent;
import np.edu.nast.payroll.Payroll.service.PayrollExtraComponentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/payroll-components")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173") // Matches your AttendanceController
public class PayrollExtraComponentController {

    private final PayrollExtraComponentService componentService;

    // Fetch breakdown for a specific payroll record
    @GetMapping("/payroll/{payrollId}")
    public ResponseEntity<List<PayrollExtraComponent>> getComponents(@PathVariable Integer payrollId) {
        return ResponseEntity.ok(componentService.getComponentsByPayrollId(payrollId));
    }

    // New: Fetch specific component by its own ID if needed
    @GetMapping("/{id}")
    public ResponseEntity<PayrollExtraComponent> getById(@PathVariable Long id) {
        return componentService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Delete a specific component line item
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeComponent(@PathVariable Long id) {
        componentService.deleteComponent(id);
        return ResponseEntity.noContent().build();
    }
}