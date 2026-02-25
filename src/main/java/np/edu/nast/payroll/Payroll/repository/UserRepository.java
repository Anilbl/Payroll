package np.edu.nast.payroll.Payroll.repository;

import np.edu.nast.payroll.Payroll.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByUsername(String username);

    // Consolidated email lookup - used by Auth, Forgot Password, and Setup
    Optional<User> findByEmailIgnoreCase(String email);

    // Fallback for strict email matching if required by specific services
    Optional<User> findByEmail(String email);

    // Needed for both Forgot Password and Initial Setup verification
    Optional<User> findByResetToken(String resetToken);
}