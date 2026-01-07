package np.edu.nast.payroll.Payroll.service.impl;

import lombok.RequiredArgsConstructor;
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
    }
}