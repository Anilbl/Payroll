package np.edu.nast.payroll.Payroll.controller;

import np.edu.nast.payroll.Payroll.salaryDTO.SalaryAnalyticsResponseDTO;
import np.edu.nast.payroll.Payroll.service.impl.SalaryAnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/salary-analytics")
// CrossOrigin should match your frontend port exactly
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class SalaryAnalyticsController {

    @Autowired
    private SalaryAnalyticsService salaryService;

    @GetMapping("/me")
    public ResponseEntity<SalaryAnalyticsResponseDTO> getMySalary(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("month") String month) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            // Use the username from the authenticated principal to fetch records
            SalaryAnalyticsResponseDTO data =
                    salaryService.getSalaryDetailsByUsername(userDetails.getUsername(), month);
            return ResponseEntity.ok(data);
        } catch (RuntimeException e) {
            // Return 404 if no payroll record exists for that specific month
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}