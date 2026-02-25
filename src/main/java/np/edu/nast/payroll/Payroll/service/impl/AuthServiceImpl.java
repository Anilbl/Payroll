package np.edu.nast.payroll.Payroll.service.impl;

import np.edu.nast.payroll.Payroll.dto.LoginRequestDTO;
import np.edu.nast.payroll.Payroll.dto.LoginResponseDTO;
import np.edu.nast.payroll.Payroll.entity.User;
import np.edu.nast.payroll.Payroll.entity.Employee;
import np.edu.nast.payroll.Payroll.repository.UserRepository;
import np.edu.nast.payroll.Payroll.repository.EmployeeRepository;
import np.edu.nast.payroll.Payroll.security.JwtUtils;
import np.edu.nast.payroll.Payroll.service.AuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final JwtUtils jwtUtils;

    public AuthServiceImpl(AuthenticationManager authenticationManager,
                           UserRepository userRepository,
                           EmployeeRepository employeeRepository,
                           JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.employeeRepository = employeeRepository;
        this.jwtUtils = jwtUtils;
    }

    @Override
    @Transactional(readOnly = true)
    public LoginResponseDTO authenticateUser(LoginRequestDTO request) {
        String inputUsername = request.getUsername().trim();

        // 1. Pre-fetch user to check state if auth fails
        User user = userRepository.findByUsername(inputUsername)
                .orElseThrow(() -> new RuntimeException("User not found: " + inputUsername));

        try {
            // 2. Attempt Authentication
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(inputUsername, request.getPassword())
            );
        } catch (BadCredentialsException e) {
            if (user.isFirstLogin()) {
                throw new RuntimeException("Initial setup required. Please use the temporary password sent to your email.");
            }
            throw new RuntimeException("Incorrect password for user: " + inputUsername);
        } catch (AuthenticationException e) {
            if (!user.isFirstLogin()) {
                throw new RuntimeException("Authentication failed for '" + inputUsername + "'. Account may be inactive.");
            }
        }

        // 3. Fetch linked Employee profile
        Employee employee = employeeRepository.findByEmail(user.getEmail())
                .orElseThrow(() -> new RuntimeException("No Employee profile linked to: " + user.getEmail()));

        // 4. Role Formatting
        String roleName = user.getRole().getRoleName().toUpperCase();
        if (!roleName.startsWith("ROLE_")) {
            roleName = "ROLE_" + roleName;
        }

        // 5. Generate Token
        String token = jwtUtils.generateToken(user.getUsername(), roleName);

        // 6. Return DTO with all permission flags included
        // Mapping boolean flags directly from the User entity to the DTO
        return new LoginResponseDTO(
                user.getUserId(),
                employee.getEmpId(),
                user.getUsername(),
                user.getEmail(),
                roleName,
                token,
                user.isFirstLogin(),
                user.isAdmin(),           // Flag for Admin Portal
                user.isAccountant(),      // Flag for Accountant Portal
                user.isHasEmployeeRole()  // Flag for Employee Portal
        );
    }
}