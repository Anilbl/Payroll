package np.edu.nast.payroll.Payroll.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDTO {

    private Integer userId; // Matches your User Entity @Id type

    // Essential for linking the login session to an Employee profile
    private Integer empId;

    private String username;
    private String email;
    private String role; // The role name from Role entity
    private String token;
}