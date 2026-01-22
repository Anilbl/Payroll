package np.edu.nast.payroll.Payroll.reportdto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyPayrollDTO {
    private String month;
    private Double totalAmount;
    private Long employeeCount;

    /**
     * Essential Constructor for JPQL Repository queries.
     * This allows Spring Data JPA to map "SELECT new MonthlyPayrollDTO(...)" results.
     */
    public MonthlyPayrollDTO(String month, Double totalAmount) {
        this.month = month;
        this.totalAmount = totalAmount;
    }
}