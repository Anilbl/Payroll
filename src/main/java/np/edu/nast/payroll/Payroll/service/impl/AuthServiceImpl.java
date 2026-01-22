package np.edu.nast.payroll.Payroll.service.impl;

import np.edu.nast.payroll.Payroll.dto.auth.LoginRequestDTO;
import np.edu.nast.payroll.Payroll.dto.auth.LoginResponseDTO;
import np.edu.nast.payroll.Payroll.entity.User;
import np.edu.nast.payroll.Payroll.repository.UserRepository;
import np.edu.nast.payroll.Payroll.security.JwtUtils;
import np.edu.nast.payroll.Payroll.service.AuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;

    public AuthServiceImpl(AuthenticationManager authenticationManager,
                           UserRepository userRepository,
                           JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.jwtUtils = jwtUtils;
    }

    @Override
    public LoginResponseDTO authenticateUser(LoginRequestDTO request) {
        try {
<<<<<<< HEAD
=======
            // 🔐 This is where Spring checks the password against the encoded password in DB
>>>>>>> 3214be41b790e5d207ff8a4a5185d56a25676df5
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );
        } catch (org.springframework.security.authentication.BadCredentialsException e) {
<<<<<<< HEAD
            throw new RuntimeException("Invalid credentials: The password you entered is incorrect.");
        } catch (org.springframework.security.core.AuthenticationException e) {
=======
            // Explicitly catch wrong password/username
            throw new RuntimeException("Invalid credentials: The password you entered is incorrect.");
        } catch (org.springframework.security.core.AuthenticationException e) {
            // Catch other security issues (Account locked, disabled, etc.)
>>>>>>> 3214be41b790e5d207ff8a4a5185d56a25676df5
            throw new RuntimeException("Authentication failed: " + e.getMessage());
        }

        User user = userRepository.findByUsername(request.getUsername())
<<<<<<< HEAD
                .orElseThrow(() -> new RuntimeException("User not found in database."));
=======
                .orElseThrow(() -> new RuntimeException("User '" + request.getUsername() + "' not found in database."));
>>>>>>> 3214be41b790e5d207ff8a4a5185d56a25676df5

        String roleName = user.getRole().getRoleName().toUpperCase();
        if (!roleName.startsWith("ROLE_")) {
            roleName = "ROLE_" + roleName;
        }

        String token = jwtUtils.generateToken(user.getUsername(), roleName);

<<<<<<< HEAD
        // This now works because User.java has empId
        return new LoginResponseDTO(
                user.getUserId(),
                user.getEmpId(), // Returns 13 for shyam_worker
=======
        return new LoginResponseDTO(
                user.getUserId(),
>>>>>>> 3214be41b790e5d207ff8a4a5185d56a25676df5
                user.getUsername(),
                user.getEmail(),
                roleName,
                token
        );
    }
<<<<<<< HEAD
}
=======
}
>>>>>>> 3214be41b790e5d207ff8a4a5185d56a25676df5
