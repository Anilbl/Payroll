package np.edu.nast.payroll.Payroll.controller;

import lombok.RequiredArgsConstructor;
import np.edu.nast.payroll.Payroll.dto.auth.SalarySummaryDTO;
import np.edu.nast.payroll.Payroll.service.SalarySummaryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class SalarySummaryController {

    private final SalarySummaryService salarySummaryService;

    /** * REMOVED getDashboardData() from here because it conflicts with
     * SalaryDashboardController.getCommandCenterStats()
     **/

    // This handles: GET /api/payrolls/summary
    @GetMapping("/payrolls/summary")
    public ResponseEntity<SalarySummaryDTO> getSummary() {
        return ResponseEntity.ok(salarySummaryService.getSummaryData());
    }
}