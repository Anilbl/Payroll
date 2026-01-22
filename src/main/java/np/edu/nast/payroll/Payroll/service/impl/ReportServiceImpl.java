package np.edu.nast.payroll.Payroll.service.impl;

import lombok.RequiredArgsConstructor;
<<<<<<< HEAD
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

    @Override
    public List<MonthlyPayrollDTO> getMonthlyPayroll(int year) {
        return repo.monthlyPayroll(year);
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
                0L
        );
    }

    @Override
    public List<Report> getAllReports() {
        return List.of(); // Implement if needed for UI history
=======
import np.edu.nast.payroll.Payroll.dto.auth.SalaryReportDTO;
import np.edu.nast.payroll.Payroll.entity.Employee;
import np.edu.nast.payroll.Payroll.entity.Report;
import np.edu.nast.payroll.Payroll.repository.EmployeeRepository;
import np.edu.nast.payroll.Payroll.repository.ReportRepository;
import np.edu.nast.payroll.Payroll.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ReportServiceImpl implements ReportService {
    private final ReportRepository repo;

    @Override
    public List<Report> getAllReports() {
        return List.of();
    }

    @Override
    public void generateAndSaveReport(String category) {

    }

    @Override
    public Report getReportById(Long id) {
        return null;
    }

    @Override
    public byte[] getFileData(String filePath) {
        return new byte[0];
    }

    @Override
    public List<SalaryReportDTO> getSalarySummaryData() {
        return List.of();
>>>>>>> 3214be41b790e5d207ff8a4a5185d56a25676df5
    }
}