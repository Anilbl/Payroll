package np.edu.nast.payroll.Payroll.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LoginResponseDTO {
    private Integer userId;
    private Integer empId;
    private String username;
    private String email;
    private String role;
    private String token;

    // Use @JsonProperty to force the exact naming in JSON
    @JsonProperty("isFirstLogin")
    private boolean isFirstLogin;

    @JsonProperty("isAdmin")
    private boolean isAdmin;

    @JsonProperty("isAccountant")
    private boolean isAccountant;

    @JsonProperty("hasEmployeeRole")
    private boolean hasEmployeeRole;

    public LoginResponseDTO(Integer userId, Integer empId, String username,
                            String email, String role, String token,
                            boolean isFirstLogin, boolean isAdmin,
                            boolean isAccountant, boolean hasEmployeeRole) {
        this.userId = userId;
        this.empId = empId;
        this.username = username;
        this.email = email;
        this.role = role;
        this.token = token;
        this.isFirstLogin = isFirstLogin;
        this.isAdmin = isAdmin;
        this.isAccountant = isAccountant;
        this.hasEmployeeRole = hasEmployeeRole;
    }
}