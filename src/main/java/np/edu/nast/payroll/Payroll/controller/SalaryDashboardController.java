package np.edu.nast.payroll.Payroll.controller;

import np.edu.nast.payroll.Payroll.dto.auth.SalarySummaryDTO;
import np.edu.nast.payroll.Payroll.entity.Payroll;
import np.edu.nast.payroll.Payroll.service.PayrollService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/payrolls") // Matches frontend: api.get('/payrolls/summary')
@CrossOrigin(origins = "http://localhost:5173")
public class SalaryDashboardController {

    @Autowired
    private PayrollService payrollService;

    @GetMapping("/summary")
    public SalarySummaryDTO getSummary() {
        List<Payroll> allPayrolls = payrollService.getAll();

        SalarySummaryDTO dto = new SalarySummaryDTO();
        dto.departments = new ArrayList<>();

        if (allPayrolls == null || allPayrolls.isEmpty()) return dto;

        // Calculate Totals
        dto.totalGross = allPayrolls.stream().mapToDouble(p -> p.getGrossSalary() != null ? p.getGrossSalary() : 0.0).sum();
        dto.totalDeductions = allPayrolls.stream().mapToDouble(p -> p.getTotalDeductions() != null ? p.getTotalDeductions() : 0.0).sum();
        dto.totalNet = allPayrolls.stream().mapToDouble(p -> p.getNetSalary() != null ? p.getNetSalary() : 0.0).sum();

        // Group by Department
        Map<String, List<Payroll>> groupedByDept = allPayrolls.stream()
                .filter(p -> p.getEmployee() != null && p.getEmployee().getDepartment() != null)
                .collect(Collectors.groupingBy(p -> p.getEmployee().getDepartment().getDeptName()));

        groupedByDept.forEach((name, list) -> {
            double net = list.stream().mapToDouble(p -> p.getNetSalary() != null ? p.getNetSalary() : 0.0).sum();
            double tax = list.stream().mapToDouble(p -> p.getTotalTax() != null ? p.getTotalTax() : 0.0).sum();
            dto.departments.add(new SalarySummaryDTO.DeptBreakdown(name, net, tax));
        });

        return dto;
    }
}