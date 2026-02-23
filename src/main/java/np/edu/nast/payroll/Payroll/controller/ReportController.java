package np.edu.nast.payroll.Payroll.controller;

import np.edu.nast.payroll.Payroll.entity.Report;
import np.edu.nast.payroll.Payroll.reportdto.AttendanceSummaryDTO;
import np.edu.nast.payroll.Payroll.reportdto.MonthlyPayrollDTO;
import np.edu.nast.payroll.Payroll.reportdto.PayrollSummaryDTO;
import np.edu.nast.payroll.Payroll.repository.AttendanceRepository;
import np.edu.nast.payroll.Payroll.repository.EmployeeRepository;
import np.edu.nast.payroll.Payroll.repository.LeaveBalanceRepository;
import np.edu.nast.payroll.Payroll.repository.PayrollRepository;
import np.edu.nast.payroll.Payroll.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping("/history")
    public List<Report> getHistory() {
        return reportService.getAllReports();
    }

    @PostMapping("/generate")
    public ResponseEntity<String> generate(@RequestParam String category) {
        reportService.generateAndSaveReport(category);
        return ResponseEntity.ok("Generated");
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> download(@PathVariable Long id) {
        Report report = reportService.getReportById(id);
        byte[] data = reportService.getFileData(report.getFilePath());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + report.getFileName())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(data);
    }

    //reports charts
    private final PayrollRepository payrollRepo;
    private final EmployeeRepository employeeRepo;
    private final LeaveBalanceRepository leaveBalanceRepo;
    private final AttendanceRepository attendanceRepo;

    // 1️⃣ Summary cards
    @GetMapping("/analytics/summary")
    public PayrollSummaryDTO summary(@RequestParam int year) {
        // Use the Builder to satisfy the updated DTO structure
        // while maintaining the existing yearly analytics logic.
        return PayrollSummaryDTO.builder()
                .totalEmployees(employeeRepo.count())
                .totalNet(payrollRepo.yearlyPayroll(year)) // yearlyPayroll returns SUM(netSalary)
                .totalDeductions(payrollRepo.yearlyDeductions(year))
                .totalAllowances(payrollRepo.yearlyAllowances(year))
                .pendingLeaves(leaveBalanceRepo.countByCurrentBalanceDaysGreaterThan(0))
                // Optional: Initialize UI fields to default values for this specific endpoint
                .totalGross(0.0)
                .totalTax(0.0)
                .totalSSF(0.0)
                .totalOvertime(0.0)
                .paidCount(0L)
                .departments(java.util.List.of())
                .build();
    }

    // 2️⃣ Monthly payroll chart
    @GetMapping("/analytics/monthly-payroll")
    public List<MonthlyPayrollDTO> monthlyPayroll(@RequestParam int year) {
        return payrollRepo.monthlyPayroll(year);
    }

    @GetMapping("/attendance/summary")
    public AttendanceSummaryDTO attendance(
            @RequestParam int year,
            @RequestParam int month) {

        List<Object[]> result = attendanceRepo.summary(year, month);

        long present = 0, absent = 0, leave = 0;

        if (!result.isEmpty()) {
            Object[] r = result.get(0);

            if (r.length > 0 && r[0] != null) present = ((Number) r[0]).longValue();
            if (r.length > 1 && r[1] != null) absent = ((Number) r[1]).longValue();
            if (r.length > 2 && r[2] != null) leave = ((Number) r[2]).longValue();
        }

        return new AttendanceSummaryDTO(present, absent, leave);
    }
}


