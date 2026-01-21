package np.edu.nast.payroll.Payroll.controller;

import np.edu.nast.payroll.Payroll.entity.Payroll;
import np.edu.nast.payroll.Payroll.service.PayrollService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payrolls")
@CrossOrigin(origins = "http://localhost:5173")
public class PayrollController {

    @Autowired
    private PayrollService payrollService;

    @GetMapping
    public List<Payroll> getAll() {
        // Main Dashboard: Current month only
        return payrollService.getAllPayrolls();
    }

    @GetMapping("/employee/{empId}/history")
    public List<Payroll> getEmployeeHistory(@PathVariable Integer empId) {
        // Professional drill-down for 12-month data
        return payrollService.getEmployeeHistory(empId);
    }

    @PutMapping("/{id}/void")
    public ResponseEntity<Payroll> voidPayroll(@PathVariable Integer id, @RequestBody Map<String, String> request) {
        String remarks = request.get("remarks");
        Payroll voidedPayroll = payrollService.voidPayroll(id, remarks);
        return ResponseEntity.ok(voidedPayroll);
    }
}