package np.edu.nast.payroll.Payroll.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDTO {
    private String firstName;
    private String lastName;
    private String designation;
    private String attendanceRate;
    private Integer remainingLeaves;
    private Double lastSalary;
}