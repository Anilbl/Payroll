package np.edu.nast.payroll.Payroll.salaryDTO;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SalaryAnalyticsResponseDTO {
            // Employee datils
            String  employeeName;
            String designation;
            String employmentStatus;
            // Bank Details
            String bankName;
            String bankAccount;
            // salary BreakDown
            Double baseSalary;
            Double grossSalary;
            Double totalAllowances;
            Double totalDeductions;
            Double taxableAmount;
            Double netSalary;


}
