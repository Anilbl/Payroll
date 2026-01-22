package np.edu.nast.payroll.Payroll.controller;

<<<<<<< HEAD
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;
import np.edu.nast.payroll.Payroll.entity.Report;
import np.edu.nast.payroll.Payroll.repository.ReportFileRepository;
import np.edu.nast.payroll.Payroll.reportdto.MonthlyPayrollDTO;
import np.edu.nast.payroll.Payroll.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
=======
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
>>>>>>> 3214be41b790e5d207ff8a4a5185d56a25676df5
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

<<<<<<< HEAD
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
=======
>>>>>>> 3214be41b790e5d207ff8a4a5185d56a25676df5
import java.util.List;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class ReportController {

<<<<<<< HEAD
    private final ReportService reportService;
    private final ReportFileRepository reportFileRepository;

    @PostMapping("/generate")
    public ResponseEntity<Report> generateReport(@RequestParam String category) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            document.add(new Paragraph("NAST Payroll Management System").setBold().setFontSize(18));
            document.add(new Paragraph(category + " Report").setFontSize(14));
            document.add(new Paragraph("Generated: " + LocalDateTime.now()));
            document.add(new Paragraph("\n"));

            if (category.equalsIgnoreCase("Salary Summaries")) {
                List<MonthlyPayrollDTO> data = reportService.getMonthlyPayroll(2026);
                Table table = new Table(UnitValue.createPercentArray(new float[]{5, 5})).useAllAvailableWidth();
                table.addHeaderCell("Month");
                table.addHeaderCell("Total Net Salary (NPR)");
                for (MonthlyPayrollDTO item : data) {
                    table.addCell(item.getMonth() != null ? item.getMonth() : "N/A");
                    table.addCell(String.format("%.2f", item.getTotalAmount()));
                }
                document.add(table);
            } else if (category.equalsIgnoreCase("Tax & SSF Reports")) {
                double deductions = reportService.sumDeductions(2026);
                Table table = new Table(UnitValue.createPercentArray(new float[]{7, 3})).useAllAvailableWidth();
                table.addHeaderCell("Description");
                table.addHeaderCell("Total NPR");
                table.addCell("Statutory Deductions (Tax/SSF)");
                table.addCell(String.format("%.2f", deductions));
                document.add(table);
            } else if (category.equalsIgnoreCase("Attendance Logs")) {
                long count = reportService.countEmployees();
                document.add(new Paragraph("Active employees for cycle: " + count));
            }

            document.close();
            byte[] pdfBytes = baos.toByteArray();

            // File persistence logic
            String fileName = category.replace(" ", "_") + "_" + System.currentTimeMillis() + ".pdf";
            String dir = System.getProperty("user.home") + File.separator + "payroll_reports" + File.separator;
            new File(dir).mkdirs();
            Path path = Paths.get(dir + fileName);
            Files.write(path, pdfBytes);

            Report newReport = Report.builder()
                    .fileName(fileName).category(category).dateGenerated(LocalDateTime.now())
                    .fileSize((pdfBytes.length / 1024) + " KB").filePath(path.toString()).build();

            return ResponseEntity.ok(reportFileRepository.save(newReport));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/history")
    public List<Report> getHistory() {
        return reportFileRepository.findAllByOrderByDateGeneratedDesc();
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> downloadReport(@PathVariable Long id) {
        try {
            Report report = reportFileRepository.findById(id).orElseThrow();
            Path path = Paths.get(report.getFilePath());
            Resource resource = new UrlResource(path.toUri());
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + report.getFileName() + "\"")
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
=======
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

        return new PayrollSummaryDTO(
                employeeRepo.count(),
                payrollRepo.yearlyPayroll(year),
                payrollRepo.yearlyDeductions(year),
                payrollRepo.yearlyAllowances(year),
                leaveBalanceRepo.countByCurrentBalanceDaysGreaterThan(0)
        );

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


>>>>>>> 3214be41b790e5d207ff8a4a5185d56a25676df5
