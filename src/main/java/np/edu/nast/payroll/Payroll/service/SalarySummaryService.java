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

    public SalarySummaryDTO getSummaryData() {
        SalarySummaryDTO dto = new SalarySummaryDTO();

        // FIX: Ensure null values from DB are converted to 0.0 to prevent NPE
        Double gross = payrollRepository.sumTotalGross();
        Double deductions = payrollRepository.sumTotalDeductions();
        Double net = payrollRepository.sumTotalNet();

        dto.setTotalGross(gross != null ? gross : 0.0);
        dto.setTotalDeductions(deductions != null ? deductions : 0.0);
        dto.setTotalNet(net != null ? net : 0.0);

        // Dashboard calculation logic
        dto.setMonthlyPayrollTotal(dto.getTotalNet());

        // Match status string to your DB Enum: 'Paid', 'Processed', or 'Pending'
        dto.setPayrollStatus("Live");
        dto.setCompliancePercentage(100);

        // FIX: Explicitly cast long count to int for the DTO
        long pendingCount = payrollRepository.countByStatus("PENDING");
        dto.setPendingVerifications((int) pendingCount);

        // FIX: Initialize list to avoid "cannot read properties of null (reading 'map')" in React
        dto.setDepartments(new ArrayList<>());

        return dto;
    }
}