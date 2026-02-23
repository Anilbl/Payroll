package np.edu.nast.payroll.Payroll.reportdto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DepartmentSummaryDTO {
    private String name;
    private long totalEmployees; // All employees assigned to this dept
    private long paidCount;
    private double net;
    private double tax;
}