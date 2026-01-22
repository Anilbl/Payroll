package np.edu.nast.payroll.Payroll.repository;

import np.edu.nast.payroll.Payroll.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);

    // Needed for Forgot Password
    Optional<User> findByEmailIgnoreCase(String email);
<<<<<<< HEAD
    // For validation checks
    Boolean existsByEmail(String email);
=======

>>>>>>> 3214be41b790e5d207ff8a4a5185d56a25676df5
    // Needed for Reset Password
    Optional<User> findByResetToken(String resetToken);
}