package np.edu.nast.payroll.Payroll.controller;

import np.edu.nast.payroll.Payroll.dto.auth.SalarySummaryDTO;
import np.edu.nast.payroll.Payroll.dto.auth.CommandCenterDTO;
import np.edu.nast.payroll.Payroll.entity.SalaryComponent;
import np.edu.nast.payroll.Payroll.entity.Department;
import np.edu.nast.payroll.Payroll.service.SalaryComponentService;
import np.edu.nast.payroll.Payroll.service.DepartmentService;
import np.edu.nast.payroll.Payroll.service.GlobalSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/salary-summary")
@CrossOrigin(origins = "http://localhost:5173")
public class SalaryDashboardController {

    @Autowired
    private SalaryComponentService componentService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private GlobalSettingService settingsService;

    // URL: GET /api/salary-summary
    @GetMapping
    public SalarySummaryDTO getSummary() {
        List<SalaryComponent> components = componentService.getAll();
        List<Department> departments = departmentService.getAll();

        int earningTypeId = Integer.parseInt(settingsService.getValue("TYPE_ID_EARNING", "1"));
        int deductionTypeId = Integer.parseInt(settingsService.getValue("TYPE_ID_DEDUCTION", "3"));

        SalarySummaryDTO dto = new SalarySummaryDTO();
        dto.setDepartments(new ArrayList<>());

        double totalGross = 0;
        double totalDeductions = 0;

        for (Department dept : departments) {
            String deptIdStr = String.valueOf(dept.getDeptId());

            double deptGross = components.stream()
                    .filter(c -> deptIdStr.equals(c.getDescription()) &&
                            c.getComponentType() != null &&
                            c.getComponentType().getComponentTypeId() == earningTypeId)
                    .mapToDouble(SalaryComponent::getDefaultValue).sum();

            double deptTax = components.stream()
                    .filter(c -> deptIdStr.equals(c.getDescription()) &&
                            c.getComponentType() != null &&
                            c.getComponentType().getComponentTypeId() == deductionTypeId)
                    .mapToDouble(SalaryComponent::getDefaultValue).sum();

            if (deptGross > 0 || deptTax > 0) {
                totalGross += deptGross;
                totalDeductions += deptTax;
                dto.getDepartments().add(new SalarySummaryDTO.DeptBreakdown(
                        dept.getDeptName(),
                        deptGross - deptTax,
                        deptTax
                ));
            }
        }

        dto.setTotalGross(totalGross);
        dto.setTotalDeductions(totalDeductions);
        dto.setTotalNet(totalGross - totalDeductions);

        return dto;
    }

    // URL: GET /api/salary-summary/command-center
    @GetMapping("/command-center")
    public CommandCenterDTO getCommandCenterStats() {
        List<SalaryComponent> components = componentService.getAll();
        CommandCenterDTO dto = new CommandCenterDTO();

        int earningTypeId = Integer.parseInt(settingsService.getValue("TYPE_ID_EARNING", "1"));

        double totalPayroll = components.stream()
                .filter(c -> c.getComponentType() != null &&
                        c.getComponentType().getComponentTypeId() == earningTypeId)
                .mapToDouble(SalaryComponent::getDefaultValue).sum();

        long pendingCount = components.stream()
                .filter(c -> c.getRequired() != null && c.getRequired())
                .count();

        dto.setMonthlyPayrollTotal(totalPayroll);
        dto.setPayrollStatus("Processing");
        dto.setCompliancePercentage(100);
        dto.setPendingVerifications((int) pendingCount);

        return dto;
    }
}