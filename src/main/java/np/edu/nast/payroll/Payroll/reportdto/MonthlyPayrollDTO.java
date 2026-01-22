package np.edu.nast.payroll.Payroll.reportdto;

import lombok.AllArgsConstructor;
import lombok.Data;
<<<<<<< HEAD
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyPayrollDTO {
    private String month;
    private Double totalAmount;
    private Long employeeCount;

    // This constructor must exist for your JPQL 'new' query in the Repository
    public MonthlyPayrollDTO(String month, Double totalAmount) {
        this.month = month;
        this.totalAmount = totalAmount;
    }
}
=======

@Data

public class MonthlyPayrollDTO {
    private String month;
    private double amount;
   public MonthlyPayrollDTO(String month, double amount) {
       this.month = month;
       this.amount = amount;
   }
}
>>>>>>> 3214be41b790e5d207ff8a4a5185d56a25676df5
