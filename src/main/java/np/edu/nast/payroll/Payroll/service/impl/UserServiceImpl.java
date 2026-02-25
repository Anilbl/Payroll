package np.edu.nast.payroll.Payroll.service.impl;

import lombok.RequiredArgsConstructor;
import np.edu.nast.payroll.Payroll.entity.User;
import np.edu.nast.payroll.Payroll.exception.EmailAlreadyExistsException;
import np.edu.nast.payroll.Payroll.exception.ResourceNotFoundException;
import np.edu.nast.payroll.Payroll.repository.UserRepository;
import np.edu.nast.payroll.Payroll.service.EmailService;
import np.edu.nast.payroll.Payroll.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.security.SecureRandom;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // Now using BCrypt
    private final EmailService emailService;

    // =========================================================
    // CREATE USER (Admin Side)
    // =========================================================
    @Override
    public User create(User user) {
        if (userRepository.findByEmailIgnoreCase(user.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException(
                    "A user with the " + user.getEmail() + " email already exists."
            );
        }

        // 1. Generate a plain text temporary password
        String tempPassword = generateRandomString(10);

        // 2. BCrypt encode it before saving
        user.setPassword(passwordEncoder.encode(tempPassword));
        user.setFirstLogin(true);
        user.setStatus("ACTIVE");

        /* Entity's @PrePersist handles isAdmin, isAccountant, hasEmployeeRole */
        User savedUser = userRepository.save(user);

        // 3. Send the PLAIN TEXT temporary password to the user via email
        emailService.sendSimpleEmail(
                savedUser.getEmail(),
                "Account Created - NAST Payroll",
                "Your account has been created.\n\n" +
                        "Default Username: " + savedUser.getUsername() +
                        "\nDefault Password: " + tempPassword +
                        "\n\nPlease login to setup your permanent account."
        );

        return savedUser;
    }

    // =========================================================
    // FINALIZE ACCOUNT SETUP (First Login)
    // =========================================================
    @Override
    public void finalizeAccountSetup(String email, String newUsername, String newPassword, String token) {
        User user = userRepository.findByEmailIgnoreCase(email.trim())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        // OTP/Token Validation
        if (user.getResetToken() == null || !user.getResetToken().equals(token)) {
            throw new IllegalArgumentException("Invalid verification code.");
        }

        if (user.getTokenExpiry() != null && user.getTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Verification code has expired.");
        }

        // Update credentials
        user.setUsername(newUsername.trim());
        user.setPassword(passwordEncoder.encode(newPassword)); // BCrypt Encode

        // Mark setup as complete
        user.setFirstLogin(false);
        user.setResetToken(null);
        user.setTokenExpiry(null);

        userRepository.save(user);
    }

    // =========================================================
    // PASSWORD RESET LOGIC
    // =========================================================
    @Override
    public void initiatePasswordReset(String email) {
        User user = userRepository.findByEmailIgnoreCase(email.trim())
                .orElseThrow(() -> new ResourceNotFoundException("Email not found"));

        String otp = String.format("%06d", new SecureRandom().nextInt(999999));
        user.setResetToken(otp);
        user.setTokenExpiry(LocalDateTime.now().plusMinutes(15));

        userRepository.save(user);
        emailService.sendOtpEmail(user.getEmail(), otp);
    }

    @Override
    public void resetPassword(String token, String newPassword) {
        User user = userRepository.findByResetToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid or expired token"));

        if (user.getTokenExpiry() != null && user.getTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Reset token has expired.");
        }

        user.setPassword(passwordEncoder.encode(newPassword)); // BCrypt Encode
        user.setResetToken(null);
        user.setTokenExpiry(null);

        userRepository.save(user);
    }

    // =========================================================
    // UPDATE USER
    // =========================================================
    @Override
    public User update(Integer id, User userDetails) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        existingUser.setUsername(userDetails.getUsername());
        existingUser.setEmail(userDetails.getEmail());
        existingUser.setStatus(userDetails.getStatus());

        if (userDetails.getRole() != null) {
            existingUser.setRole(userDetails.getRole());
            /* Entity's @PreUpdate will sync boolean flags automatically */
        }

        // Only update password if a new one is provided in the request
        if (userDetails.getPassword() != null && !userDetails.getPassword().trim().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        }

        return userRepository.save(existingUser);
    }

    // =========================================================
    // HELPER METHODS
    // =========================================================
    @Override
    public boolean canSwitchRole(User user, String roleName) {
        if (user == null || roleName == null) return false;
        return switch (roleName.toUpperCase()) {
            case "ADMIN" -> user.isAdmin();
            case "ACCOUNTANT" -> user.isAccountant();
            case "EMPLOYEE" -> user.isHasEmployeeRole();
            default -> false;
        };
    }

    @Override
    public List<User> getAll() { return userRepository.findAll(); }

    @Override
    public User getById(Integer id) {
        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Override
    public void delete(Integer id) { userRepository.deleteById(id); }

    @Override
    public User getByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email).orElse(null);
    }

    private String generateRandomString(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom rnd = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        }
        return sb.toString();
    }

    @Override
    public void sendOtpToAllUsers() {
        List<User> users = userRepository.findAll();
        for (User user : users) {
            String otp = String.format("%06d", new SecureRandom().nextInt(999999));
            emailService.sendOtpEmail(user.getEmail(), otp);
        }
    }

    @Override
    public User setupDefaultAccount(Integer empId) {
        // Implementation depends on how you want to link employee to user specifically
        return new User();
    }
}