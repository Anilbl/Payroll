package np.edu.nast.payroll.Payroll.service;

import np.edu.nast.payroll.Payroll.entity.Role;
import java.util.List;

public interface RoleService {
    Role saveRole(Role role);
    Role updateRole(Role role);
    void deleteRole(Integer roleId);
    Role getRoleById(Integer roleId);
    List<Role> getAllRoles();
<<<<<<< HEAD
}
=======
}
>>>>>>> 3214be41b790e5d207ff8a4a5185d56a25676df5
