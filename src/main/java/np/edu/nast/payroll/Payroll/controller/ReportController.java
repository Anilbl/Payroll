package np.edu.nast.payroll.Payroll.controller;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;
import lombok.RequiredArgsConstructor;
import np.edu.nast.payroll.Payroll.entity.Report;
import np.edu.nast.payroll.Payroll.reportdto.AttendanceSummaryDTO;
import np.edu.nast.payroll.Payroll.reportdto.MonthlyPayrollDTO;
import np.edu.nast.payroll.Payroll.repository.*;
import np.edu.nast.payroll.Payroll.service.ReportService;
import np.edu.nast.payroll.Payroll.service.GlobalSettingService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;
    private final ReportFileRepository reportFileRepository;
    private final AttendanceRepository attendanceRepo;
    private final GlobalSettingService settingsService;

    // ==========================================
    // 1. PDF GENERATION & FILE MANAGEMENT
    // ==========================================

    @PostMapping("/generate")
    public ResponseEntity<Report> generateReport(@RequestParam String category) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            String companyName = settingsService.getValue("COMPANY_NAME", "NAST Payroll System");

            document.add(new Paragraph(companyName).setBold().setFontSize(18));
            document.add(new Paragraph(category + " Report").setFontSize(14));
            document.add(new Paragraph("Generated: " + LocalDateTime.now()));
            document.add(new Paragraph("\n"));

            if (category.equalsIgnoreCase("Salary Summaries")) {
                generateSalaryTable(document);
            } else if (category.equalsIgnoreCase("Tax & SSF Reports")) {
                generateTaxTable(document);
            }

            document.close();
            byte[] pdfBytes = baos.toByteArray();

            String defaultDir = System.getProperty("user.home") + File.separator + "payroll_reports";
            String dirPath = settingsService.getValue("REPORT_STORAGE_PATH", defaultDir);

            String fileName = category.replace(" ", "_") + "_" + System.currentTimeMillis() + ".pdf";
            new File(dirPath).mkdirs();
            Path path = Paths.get(dirPath, fileName);
            Files.write(path, pdfBytes);

            Report newReport = Report.builder()
                    .fileName(fileName)
                    .category(category)
                    .dateGenerated(LocalDateTime.now())
                    .fileSize((pdfBytes.length / 1024) + " KB")
                    .filePath(path.toString())
                    .build();

            return ResponseEntity.ok(reportFileRepository.save(newReport));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    private void generateSalaryTable(Document document) {
        List<MonthlyPayrollDTO> data = reportService.getMonthlyPayroll(LocalDate.now().getYear());
        Table table = new Table(UnitValue.createPercentArray(new float[]{5, 5})).useAllAvailableWidth();
        table.addHeaderCell("Month");
        table.addHeaderCell("Total Net Salary (NPR)");
        for (MonthlyPayrollDTO item : data) {
            table.addCell(item.getMonth() != null ? item.getMonth() : "N/A");
            table.addCell(String.format("%.2f", item.getTotalAmount()));
        }
        document.add(table);
    }

    private void generateTaxTable(Document document) {
        double deductions = reportService.sumDeductions(LocalDate.now().getYear());
        Table table = new Table(UnitValue.createPercentArray(new float[]{7, 3})).useAllAvailableWidth();
        table.addHeaderCell("Description");
        table.addHeaderCell("Total NPR");
        table.addCell("Statutory Deductions (Tax/SSF)");
        table.addCell(String.format("%.2f", deductions));
        document.add(table);
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
            return ResponseEntity.status(404).build();
        }
    }

    // ==========================================
    // 2. ANALYTICS (FIXED CONFLICT)
    // ==========================================

    // UPDATED PATH: URL: http://localhost:8080/api/reports/daily-stats
    @GetMapping("/daily-stats")
    public AttendanceSummaryDTO attendance(@RequestParam int year, @RequestParam int month) {
        List<Object[]> result = attendanceRepo.summary(year, month);
        long present = 0, absent = 0, leave = 0;

        if (!result.isEmpty()) {
            Object[] r = result.get(0);
            if (r.length > 0 && r[0] != null) present = ((Number) r[0]).longValue();
            if (r.length > 1 && r[1] != null) absent = ((Number) r[1]).longValue();
            if (r.length > 2 && r[2] != null) leave = ((Number) r[2]).longValue();
        }

        return new AttendanceSummaryDTO(present, absent, leave, 0L, 0.0, 0.0);
    }

    @GetMapping("/history")
    public List<Report> getHistory() {
        return reportFileRepository.findAllByOrderByDateGeneratedDesc();
    }
}