package np.edu.nast.payroll.Payroll.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmployeePayrollRowDTO {
    private Integer empId;
    private String fullName;
    private Double basicSalary; // Base from contract
    private Double earnedSalary; // Actual for the month
    private Integer payrollId; // Null if not in DB
    private Double festivalBonus;
    private Double otherBonuses;
    private Double citContribution;
    private String status; // "PAID", "PENDING_PAYMENT", "READY", or "NO_EARNINGS"
}