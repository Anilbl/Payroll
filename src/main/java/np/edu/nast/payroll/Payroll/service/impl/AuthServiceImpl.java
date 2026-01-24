package np.edu.nast.payroll.Payroll.service.impl;

import np.edu.nast.payroll.Payroll.dto.auth.LoginRequestDTO;
import np.edu.nast.payroll.Payroll.dto.auth.LoginResponseDTO;
import np.edu.nast.payroll.Payroll.entity.User;
import np.edu.nast.payroll.Payroll.entity.Employee; // Import Employee
import np.edu.nast.payroll.Payroll.repository.UserRepository;
import np.edu.nast.payroll.Payroll.repository.EmployeeRepository; // Import EmployeeRepo
import np.edu.nast.payroll.Payroll.security.JwtUtils;
import np.edu.nast.payroll.Payroll.service.AuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository; // NEW
    private final JwtUtils jwtUtils;

    public AuthServiceImpl(AuthenticationManager authenticationManager,
                           UserRepository userRepository,
                           EmployeeRepository employeeRepository, // Inject this
                           JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.employeeRepository = employeeRepository;
        this.jwtUtils = jwtUtils;
    }

    @Override
    public LoginResponseDTO authenticateUser(LoginRequestDTO request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );
        } catch (org.springframework.security.authentication.BadCredentialsException e) {
            throw new RuntimeException("Invalid credentials: The password you entered is incorrect.");
        } catch (org.springframework.security.core.AuthenticationException e) {
            throw new RuntimeException("Authentication failed: " + e.getMessage());
        }

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User '" + request.getUsername() + "' not found."));

        // 🔍 FIND THE EMPLOYEE ID BY EMAIL
        // This ensures the leave system gets the real Employee ID, not just the User ID
        Employee employee = employeeRepository.findByEmail(user.getEmail())
                .orElseThrow(() -> new RuntimeException("No Employee profile linked to email: " + user.getEmail()));

        String roleName = user.getRole().getRoleName().toUpperCase();
        if (!roleName.startsWith("ROLE_")) {
            roleName = "ROLE_" + roleName;
        }

        String token = jwtUtils.generateToken(user.getUsername(), roleName);

        // Return the full object including the critical empId
        return new LoginResponseDTO(
                user.getUserId(),
                employee.getEmpId(), // Passing the actual Employee ID
                user.getUsername(),
                user.getEmail(),
                roleName,
                token
        );
    }
}