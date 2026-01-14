package np.edu.nast.payroll.Payroll.service.impl;

import np.edu.nast.payroll.Payroll.entity.Role;
import np.edu.nast.payroll.Payroll.repository.RoleRepository;
import np.edu.nast.payroll.Payroll.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public Role saveRole(Role role) {
        return roleRepository.save(role); // Returns managed entity with ID
    }

    @Override
    public Role updateRole(Role role) {
        if (roleRepository.existsById(role.getRoleId())) {
            return roleRepository.save(role);
        }
        return null;
    }

    @Override
    public void deleteRole(Integer roleId) {
        roleRepository.deleteById(roleId);
    }

    @Override
    public Role getRoleById(Integer roleId) {
        Optional<Role> role = roleRepository.findById(roleId);
        return role.orElse(null);
    }

    @Override
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    // ✅ Seed default roles at startup
    @PostConstruct
    public void seedDefaultRoles() {
        String[] defaults = {"Admin", "HR", "Accountant", "Employee"};
        for (String roleName : defaults) {
            if (!roleRepository.existsByRoleName(roleName)) {
                Role role = new Role();
                role.setRoleName(roleName);
                roleRepository.save(role);
            }
        }
    }
}
