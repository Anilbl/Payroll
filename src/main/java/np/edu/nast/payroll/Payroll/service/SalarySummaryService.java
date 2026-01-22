package np.edu.nast.payroll.Payroll.service;

import lombok.RequiredArgsConstructor;
import np.edu.nast.payroll.Payroll.dto.auth.SalarySummaryDTO;
import np.edu.nast.payroll.Payroll.repository.PayrollRepository;
import org.springframework.stereotype.Service;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class SalarySummaryService {

    private final PayrollRepository payrollRepository;
    private final GlobalSettingService settingsService;

    public SalarySummaryDTO getSummaryData() {
        // Fetch dynamic config from DB FIRST
        String liveStatusLabel = settingsService.getValue("DASHBOARD_LIVE_STATUS", "System Live");
        String pendingStatusKey = settingsService.getValue("STATUS_KEY_PENDING", "PENDING");
        int defaultCompliance = Integer.parseInt(settingsService.getValue("DEFAULT_COMPLIANCE_RATE", "100"));

        // Fetch Financial Totals
        Double gross = payrollRepository.sumTotalGross();
        Double deductions = payrollRepository.sumTotalDeductions();
        Double net = payrollRepository.sumTotalNet();

        // Count pending using the dynamic key from DB
        long pendingCount = payrollRepository.countByStatus(pendingStatusKey);

        // Build the DTO using the Builder pattern (Zero Hardcoding)
        return SalarySummaryDTO.builder()
                .totalGross(gross != null ? gross : 0.0)
                .totalDeductions(deductions != null ? deductions : 0.0)
                .totalNet(net != null ? net : 0.0)
                .monthlyPayrollTotal(net != null ? net : 0.0)
                .payrollStatus(liveStatusLabel)
                .compliancePercentage(defaultCompliance)
                .pendingVerifications((int) pendingCount)
                .departments(new ArrayList<>()) // Initialized to prevent frontend crash
                .build();
    }
}