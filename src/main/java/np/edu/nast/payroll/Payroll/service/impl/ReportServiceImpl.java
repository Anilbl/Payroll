package np.edu.nast.payroll.Payroll.service.impl;

import lombok.RequiredArgsConstructor;
import np.edu.nast.payroll.Payroll.dto.auth.SalaryReportDTO;
import np.edu.nast.payroll.Payroll.entity.Report;
import np.edu.nast.payroll.Payroll.reportdto.MonthlyPayrollDTO;
import np.edu.nast.payroll.Payroll.reportdto.ReportSummaryDTO;
import np.edu.nast.payroll.Payroll.repository.ReportRepository;
import np.edu.nast.payroll.Payroll.service.ReportService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ReportRepository repo;

    // ==========================================
    // Dashboard Statistics & Aggregations
    // ==========================================

    @Override
    public List<MonthlyPayrollDTO> getMonthlyPayroll(int year) {
        return repo.monthlyPayroll(year); // Fetches SUM(net_salary) grouped by month
    }

    @Override
    public double sumDeductions(int year) {
        return repo.sumDeductions(year);
    }

    @Override
    public long countEmployees() {
        return repo.countEmployees();
    }

    @Override
    public ReportSummaryDTO getSummary(int year) {
        return new ReportSummaryDTO(
                repo.countEmployees(),
                repo.sumPayroll(year),
                repo.sumDeductions(year),
                repo.sumAllowances(year),
                0L // Placeholder for extra metrics
        );
    }

    // ==========================================
    // Report Generation & File Management
    // ==========================================

    @Override
    public List<Report> getAllReports() {
        return repo.findAll(); // Updated from List.of() to actual repository call
    }

    @Override
    public void generateAndSaveReport(String category) {
        // Implementation for PDF/Excel generation logic
    }

    @Override
    public Report getReportById(Long id) {
        return repo.findById(id).orElse(null);
    }

    @Override
    public byte[] getFileData(String filePath) {
        return new byte[0]; // Logic to read generated report bytes
    }

    @Override
    public List<SalaryReportDTO> getSalarySummaryData() {
        return List.of(); // Placeholder for detailed salary breakdown
    }
}