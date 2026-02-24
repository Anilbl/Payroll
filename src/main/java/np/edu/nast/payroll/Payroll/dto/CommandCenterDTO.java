package np.edu.nast.payroll.Payroll.dto;

import lombok.Data;

import java.util.List;

@Data
public class CommandCenterDTO {
    private double monthlyPayrollTotal;
    private String payrollStatus; // e.g., "Processing"
    private int compliancePercentage; // e.g., 100
    private int pendingVerifications; // e.g., 12
    private List<EmployeePayrollRowDTO> employeeRows;
}