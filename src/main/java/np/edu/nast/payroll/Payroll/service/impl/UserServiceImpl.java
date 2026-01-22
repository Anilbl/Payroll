package np.edu.nast.payroll.Payroll.service.impl;

import lombok.RequiredArgsConstructor;
import np.edu.nast.payroll.Payroll.entity.User;
import np.edu.nast.payroll.Payroll.repository.UserRepository;
import np.edu.nast.payroll.Payroll.service.EmailService;
import np.edu.nast.payroll.Payroll.service.UserService;
<<<<<<< HEAD
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
=======
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
>>>>>>> 3214be41b790e5d207ff8a4a5185d56a25676df5

import java.util.List;

@Service
@RequiredArgsConstructor
<<<<<<< HEAD
@Transactional // Ensures database integrity during updates
=======
>>>>>>> 3214be41b790e5d207ff8a4a5185d56a25676df5
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

<<<<<<< HEAD
    @Override
    public User create(User user) {
        // Validation: Ensure email is unique before creating user
        if (userRepository.findByEmailIgnoreCase(user.getEmail()).isPresent()) {
            throw new RuntimeException("A user with this email already exists.");
        }
        // Encode password before saving
=======
    // ===================================================
    // Business logic methods only (no UserDetailsService)
    // ===================================================

    @Override
    public User create(User user) {
        // Encode password using the configured PasswordEncoder
>>>>>>> 3214be41b790e5d207ff8a4a5185d56a25676df5
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Override
<<<<<<< HEAD
    public User getById(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    @Override
    public User update(Integer id, User userDetails) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        // Update basic fields
        existingUser.setUsername(userDetails.getUsername());
        existingUser.setEmail(userDetails.getEmail());
        existingUser.setRole(userDetails.getRole());
        existingUser.setStatus(userDetails.getStatus());

        // Update password only if a new one is provided in the request
        if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        }

        return userRepository.save(existingUser);
    }

    @Override
    public void delete(Integer id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("Cannot delete. User not found.");
        }
=======
    public void delete(Integer id) {
>>>>>>> 3214be41b790e5d207ff8a4a5185d56a25676df5
        userRepository.deleteById(id);
    }

    @Override
    public void initiatePasswordReset(String email) {
        User user = userRepository.findByEmailIgnoreCase(email.trim())
                .orElseThrow(() -> new RuntimeException("Email not found"));
<<<<<<< HEAD

        String otp = String.valueOf((int)(Math.random() * 900000 + 100000));
        user.setResetToken(otp);
        userRepository.save(user);

=======
        String otp = String.valueOf((int)(Math.random() * 900000 + 100000));
        user.setResetToken(otp);
        userRepository.save(user);
>>>>>>> 3214be41b790e5d207ff8a4a5185d56a25676df5
        emailService.sendOtpEmail(user.getEmail(), otp);
    }

    @Override
    public void resetPassword(String token, String newPassword) {
        User user = userRepository.findByResetToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid OTP"));
<<<<<<< HEAD

=======
>>>>>>> 3214be41b790e5d207ff8a4a5185d56a25676df5
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        userRepository.save(user);
    }

    @Override
    public User getByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email).orElse(null);
    }

    @Override
    public void sendOtpToAllUsers() {
<<<<<<< HEAD
        // Logic for bulk notifications if required
=======
        // Implement as needed
>>>>>>> 3214be41b790e5d207ff8a4a5185d56a25676df5
    }

    @Override
    public User setupDefaultAccount(Integer empId) {
<<<<<<< HEAD
        // Custom logic to generate a default user for a new employee
        return new User();
    }
}
=======
        return new User();
    }

}
>>>>>>> 3214be41b790e5d207ff8a4a5185d56a25676df5
