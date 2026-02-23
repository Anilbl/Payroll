package np.edu.nast.payroll.Payroll.reportdto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PayrollSummaryDTO {
    // Existing fields
    private long totalEmployees;
    private double monthlyPayroll; // This acts as Total Net Salary
    private double totalDeductions;
    private double totalAllowances;
    private long pendingLeaves;
    private double totalNet;

    // New fields for the updated UI
    private double totalGross;
    private double totalTax;
    private double totalSSF;
    private double totalOvertime;
    private long paidCount;
    private List<DepartmentSummaryDTO> departments;
}