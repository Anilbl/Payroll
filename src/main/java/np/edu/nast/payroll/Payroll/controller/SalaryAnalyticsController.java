package np.edu.nast.payroll.Payroll.controller;

import np.edu.nast.payroll.Payroll.entity.User;
import np.edu.nast.payroll.Payroll.salaryDTO.SalaryAnalyticsResponseDTO;
import np.edu.nast.payroll.Payroll.security.CustomUserDetailsService;
import np.edu.nast.payroll.Payroll.service.impl.SalaryAnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/salary-analytics")
@CrossOrigin(origins = "http://localhost:5173")
public class SalaryAnalyticsController {

    @Autowired
    private SalaryAnalyticsService salaryService;
    @GetMapping("/me")
    public ResponseEntity<SalaryAnalyticsResponseDTO> getMySalary(
            @AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails user,
            @RequestParam("month") String month) { // Added explicit name mapping

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Pass the username (which the logs show is being treated as an email)
        SalaryAnalyticsResponseDTO data =
                salaryService.getSalaryDetailsByUsername(user.getUsername(), month);

        return ResponseEntity.ok(data);
    }
}


