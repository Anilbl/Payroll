package np.edu.nast.payroll.Payroll.salaryDTO;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SalaryAnalyticsResponseDTO {
    // Employee details
    private String employeeName;
    private String designation;
    private String employmentStatus;

    // Bank Details
    private String bankName;
    private String bankAccount;

    // Salary Breakdown
    private Double baseSalary;
    private Double grossSalary;
    private Double totalAllowances;
    private Double totalDeductions;
    private Double taxableAmount; // Mapped to TDS in frontend
    private Double netSalary;
}