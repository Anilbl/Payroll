package np.edu.nast.payroll.Payroll.repository;

import np.edu.nast.payroll.Payroll.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {

    /**
     * Checks if a role name exists in the database.
     * Prevents duplicate role creation during setup.
     */
    boolean existsByRoleName(String roleName);

    /**
     * Finds a role by its name (e.g., "ROLE_ADMIN").
     * Crucial for assigning roles to new users during registration.
     */
    Optional<Role> findByRoleName(String roleName);
}