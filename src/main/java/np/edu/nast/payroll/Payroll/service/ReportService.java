package np.edu.nast.payroll.Payroll.service;

<<<<<<< HEAD
import np.edu.nast.payroll.Payroll.entity.Report;
import np.edu.nast.payroll.Payroll.reportdto.MonthlyPayrollDTO;
import np.edu.nast.payroll.Payroll.reportdto.ReportSummaryDTO;
import java.util.List;

public interface ReportService {
    List<MonthlyPayrollDTO> getMonthlyPayroll(int year);
    double sumDeductions(int year);
    long countEmployees();
    ReportSummaryDTO getSummary(int year);

    // For historical records
    List<Report> getAllReports();
=======
import np.edu.nast.payroll.Payroll.dto.auth.SalaryReportDTO;
import np.edu.nast.payroll.Payroll.entity.Report;

import java.util.List;

public interface ReportService {
    List<Report> getAllReports();
    void generateAndSaveReport(String category);
    Report getReportById(Long id);
    byte[] getFileData(String filePath);
    // This MUST match the implementation name exactly
    List<SalaryReportDTO> getSalarySummaryData();


>>>>>>> 3214be41b790e5d207ff8a4a5185d56a25676df5
}