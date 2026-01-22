package np.edu.nast.payroll.Payroll.service;

import np.edu.nast.payroll.Payroll.dto.auth.SalaryReportDTO;
import np.edu.nast.payroll.Payroll.entity.Report;
import np.edu.nast.payroll.Payroll.reportdto.MonthlyPayrollDTO;
import np.edu.nast.payroll.Payroll.reportdto.ReportSummaryDTO;

import java.util.List;

public interface ReportService {

    // --- Dashboard Aggregation Methods ---

    List<MonthlyPayrollDTO> getMonthlyPayroll(int year);

    double sumDeductions(int year);

    long countEmployees();

    ReportSummaryDTO getSummary(int year);

    // --- Report Management & Document Export ---

    List<Report> getAllReports();

    void generateAndSaveReport(String category);

    Report getReportById(Long id);

    byte[] getFileData(String filePath);

    /**
     * Retrieves detailed salary breakdown data.
     * Must match the implementation in ReportServiceImpl.
     */
    List<SalaryReportDTO> getSalarySummaryData();
}