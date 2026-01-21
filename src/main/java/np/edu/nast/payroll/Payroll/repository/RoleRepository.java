package np.edu.nast.payroll.Payroll.repository;

import np.edu.nast.payroll.Payroll.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    // This fixes the "cannot find symbol" error in RoleServiceImpl
    boolean existsByRoleName(String roleName);

    // Used for dynamic role lookup without hardcoding IDs
    Optional<Role> findByRoleName(String roleName);
}