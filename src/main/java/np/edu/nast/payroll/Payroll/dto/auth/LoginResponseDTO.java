package np.edu.nast.payroll.Payroll.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDTO {
<<<<<<< HEAD
    private Integer userId;
    private Integer empId;
=======
    private Integer userId; // Matches your User Entity @Id type
>>>>>>> 3214be41b790e5d207ff8a4a5185d56a25676df5
    private String username;
    private String email;
    private String role; // The role name from Role entity
    private String token;
}