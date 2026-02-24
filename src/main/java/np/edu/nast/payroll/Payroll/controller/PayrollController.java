package np.edu.nast.payroll.Payroll.controller;

import np.edu.nast.payroll.Payroll.dto.CommandCenterDTO;
import np.edu.nast.payroll.Payroll.dto.PayrollDashboardDTO;
import np.edu.nast.payroll.Payroll.dto.CommandCenterDTO; // Ensure this DTO exists
import np.edu.nast.payroll.Payroll.entity.Payroll;
import np.edu.nast.payroll.Payroll.reportdto.PayrollSummaryDTO;
import np.edu.nast.payroll.Payroll.service.PayrollService;
import np.edu.nast.payroll.Payroll.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

    @Autowired
    private EmailService emailService;

    /**
     * COMMAND CENTER ENDPOINT
     * This matches your React fetchData() call: api.get("/payrolls/command-center", ...)
     */
    @GetMapping("/command-center")
    public ResponseEntity<?> getCommandCenter(
            @RequestParam int month,
            @RequestParam int year) {
        try {
            System.out.println("DEBUG: Fetching Command Center for Month: " + month + ", Year: " + year);
            CommandCenterDTO dashboardData = payrollService.getCommandCenterData(month, year);
            return ResponseEntity.ok(dashboardData);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error loading command center: " + e.getMessage()));
        }
    }

    @GetMapping
    public List<Payroll> getAll() {
        return payrollService.getAllPayrolls();
    }

    /**
     * STEP 1: PREVIEW
     * Calculates payroll based on inputs, but DOES NOT save to database.
     */
    @PostMapping("/preview")
    public ResponseEntity<?> preview(@RequestBody Map<String, Object> payload) {
        try {
            System.out.println("Payroll Preview Request: " + payload);
            Payroll previewData = payrollService.calculatePreview(payload);
            return ResponseEntity.ok(previewData);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "An unexpected error occurred."));
        }
    }

    /**
     * STEP 2: PROCESS / FINALIZE
     * Performs the actual transaction and saves to the database.
     */
    @PostMapping("/process")
    public ResponseEntity<?> process(@RequestBody Map<String, Object> payload) {
        try {
            Payroll processedPayroll = payrollService.processPayroll(payload);
            return ResponseEntity.ok(processedPayroll);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/employee/{empId}/history")
    public ResponseEntity<List<Payroll>> getEmployeeHistory(@PathVariable Integer empId) {
        List<Payroll> history = payrollService.getPayrollByEmployeeId(empId);
        return ResponseEntity.ok(history);
    }

    @PutMapping("/{id}/void")
    public ResponseEntity<Payroll> voidPayroll(@PathVariable Integer id) {
        Payroll voided = payrollService.voidPayroll(id);
        return ResponseEntity.ok(voided);
    }

    /**
     * SEND EMAIL ENDPOINT
     * If you get "No static resource", it means this mapping was missed or the ID type mismatched.
     */
    @PostMapping("/{id}/send-email")
    public ResponseEntity<?> sendEmail(@PathVariable Integer id) {
        try {
            System.out.println("DEBUG: Attempting to send email for Payroll ID: " + id);
            Payroll payroll = payrollService.getPayrollById(id);
            if (payroll == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Payroll record not found for ID: " + id));
            }
            emailService.generateAndSendPayslip(payroll, "MANUAL_UI_TRIGGER");
            return ResponseEntity.ok().body(Map.of("message", "Payslip PDF email sent successfully!"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Email Error: " + e.getMessage()));
        }
    }

    @GetMapping("/salary-summary")
    public ResponseEntity<PayrollSummaryDTO> getSalarySummary(
            @RequestParam int month,
            @RequestParam int year) {
        try {
            PayrollSummaryDTO summary = payrollService.getSalarySummary(month, year);
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * FINAL DISBURSEMENT
     */
    @PostMapping("/{id}/finalize")
    public ResponseEntity<?> finalizePayroll(
            @PathVariable Integer id,
            @RequestBody Map<String, String> payload) {
        try {
            String transactionRef = payload.getOrDefault("transactionRef", "N/A");
            payrollService.finalizePayroll(id, transactionRef);
            return ResponseEntity.ok(Map.of("message", "Payroll finalized and totals updated."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", e.getMessage()));
        }
    }
}