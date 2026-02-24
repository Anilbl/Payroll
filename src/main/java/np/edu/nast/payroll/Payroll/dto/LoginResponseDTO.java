package np.edu.nast.payroll.Payroll.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LoginResponseDTO {
    private Integer userId;
    private Integer empId; // Critical for Leave logic
    private String username;
    private String email;
    private String role;
    private String token;

    // Explicit constructor to handle the specific mapping in AuthServiceImpl
    public LoginResponseDTO(Integer userId, Integer empId, String username, String email, String role, String token) {
        this.userId = userId;
        this.empId = empId;
        this.username = username;
        this.email = email;
        this.role = role;
        this.token = token;
    }
}