package np.edu.nast.payroll.Payroll.dto.auth;
<<<<<<< HEAD

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SalarySummaryDTO {
    // Standard Summary Fields
    private double totalGross;
    private double totalDeductions;
    private double totalNet;
    private List<DeptBreakdown> departments;

    // Command Center Dashboard Fields
    private double monthlyPayrollTotal;
    private String payrollStatus;
    private int compliancePercentage;
    private int pendingVerifications;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DeptBreakdown {
        private String name;
        private double net;
        private double tax;
=======
import java.util.List;

public class SalarySummaryDTO {
    public double totalGross;
    public double totalDeductions;
    public double totalNet;
    public List<DeptBreakdown> departments;

    public static class DeptBreakdown {
        public String name;
        public double net;
        public double tax;

        public DeptBreakdown(String name, double net, double tax) {
            this.name = name;
            this.net = net;
            this.tax = tax;
        }
>>>>>>> 3214be41b790e5d207ff8a4a5185d56a25676df5
    }
}