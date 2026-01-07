package np.edu.nast.payroll.Payroll.reportdto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data

public class MonthlyPayrollDTO {
    private String month;
    private double amount;
   public MonthlyPayrollDTO(String month, double amount) {
       this.month = month;
       this.amount = amount;
   }
}
