package np.edu.nast.payroll.Payroll.dto;

import lombok.Data;

@Data
public class SetupRequestDTO {
    private String email;
    private String token;       // The 6-digit verification code
    private String newUsername;
    private String newPassword;
}