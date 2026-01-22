package np.edu.nast.payroll.Payroll.repository;

import np.edu.nast.payroll.Payroll.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    // Core authentication lookup
    Optional<User> findByUsername(String username);

    // Needed for Forgot Password workflow
    Optional<User> findByEmailIgnoreCase(String email);

    // For validation checks during registration/onboarding
    Boolean existsByEmail(String email);

    // Needed for Reset Password verification via OTP/Token
    Optional<User> findByResetToken(String resetToken);
}