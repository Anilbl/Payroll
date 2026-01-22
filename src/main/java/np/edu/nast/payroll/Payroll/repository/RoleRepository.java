package np.edu.nast.payroll.Payroll.repository;

import np.edu.nast.payroll.Payroll.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
<<<<<<< HEAD
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    // This fixes the "cannot find symbol" error in RoleServiceImpl
    boolean existsByRoleName(String roleName);

    // Used for dynamic role lookup without hardcoding IDs
    Optional<Role> findByRoleName(String roleName);
}
=======

public interface RoleRepository extends JpaRepository<Role, Integer> {
    // Optional: Add a method to check existence by name
    boolean existsByRoleName(String roleName);
}
>>>>>>> 3214be41b790e5d207ff8a4a5185d56a25676df5
