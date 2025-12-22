package np.edu.nast.payroll.Payroll.repository;

import np.edu.nast.payroll.Payroll.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    // Optional: Add a method to check existence by name
    boolean existsByRoleName(String roleName);
}
