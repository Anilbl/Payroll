package np.edu.nast.payroll.Payroll.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PayrollRequest {

    @JsonProperty("empId")
    private Integer empId;

<<<<<<< HEAD
    @JsonProperty("manualBonus")
    private Double manualBonus;

=======
>>>>>>> 3214be41b790e5d207ff8a4a5185d56a25676df5
    @JsonProperty("grossSalary")
    private Double grossSalary;

    @JsonProperty("totalAllowances")
    private Double totalAllowances;

    @JsonProperty("totalDeductions")
    private Double totalDeductions;

<<<<<<< HEAD
=======
    // These fields allow the frontend to specify IDs, or the backend will use defaults
>>>>>>> 3214be41b790e5d207ff8a4a5185d56a25676df5
    @JsonProperty("accountId")
    private Integer accountId;

    @JsonProperty("paymentMethodId")
    private Integer paymentMethodId;

    @JsonProperty("payGroupId")
    private Integer payGroupId;
}