package np.edu.nast.payroll.Payroll.repository;

import np.edu.nast.payroll.Payroll.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    // Optional: Add a method to check existence by name
    boolean existsByRoleName(String roleName);
    Optional<Role> findByRoleName(String roleName);
}
