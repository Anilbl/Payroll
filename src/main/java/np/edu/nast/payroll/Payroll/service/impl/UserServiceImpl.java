package np.edu.nast.payroll.Payroll.service.impl;

import np.edu.nast.payroll.Payroll.entity.Employee;
import np.edu.nast.payroll.Payroll.entity.Role;
import np.edu.nast.payroll.Payroll.entity.User;
import np.edu.nast.payroll.Payroll.repository.EmployeeRepository;
import np.edu.nast.payroll.Payroll.repository.RoleRepository;
import np.edu.nast.payroll.Payroll.repository.UserRepository;
import np.edu.nast.payroll.Payroll.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final EmployeeRepository employeeRepo;

    public UserServiceImpl(
            UserRepository userRepo,
            RoleRepository roleRepo,
            EmployeeRepository employeeRepo
    ) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.employeeRepo = employeeRepo;
    }

    /**
     * CREATE USER
     * RULES:
     * - Employee MUST exist
     * - Employee must NOT already have a user
     * - Email is copied from employee
     */
    @Override
    public User create(User user) {

        if (user.getEmployee() == null || user.getEmployee().getEmpId() == null) {
            throw new RuntimeException("Employee ID is required");
        }

        Employee employee = employeeRepo.findById(user.getEmployee().getEmpId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        if (employee.getUser() != null) {
            throw new RuntimeException("User already exists for this employee");
        }

        Role role = roleRepo.findById(user.getRole().getRoleId())
                .orElseThrow(() -> new RuntimeException("Role not found"));

        user.setEmployee(employee);
        user.setEmail(employee.getEmail());
        user.setRole(role);
        user.setStatus(user.getStatus() != null ? user.getStatus() : "ACTIVE");
        user.setCreatedAt(LocalDateTime.now());

        employee.setUser(user); // 🔑 link both sides

        return userRepo.save(user);
    }

    /**
     * UPDATE USER
     * - Username / password / role / status only
     * - Email comes ONLY from employee
     */
    @Override
    public User update(Integer id, User user) {

        User existing = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        existing.setUsername(user.getUsername());
        existing.setPassword(user.getPassword());

        if (user.getRole() != null && user.getRole().getRoleId() != null) {
            Role role = roleRepo.findById(user.getRole().getRoleId())
                    .orElseThrow(() -> new RuntimeException("Role not found"));
            existing.setRole(role);
        }

        if (user.getStatus() != null) {
            existing.setStatus(user.getStatus());
        }

        return userRepo.save(existing);
    }

    /**
     * DELETE USER ONLY
     * - Employee remains
     */
    @Override
    public void delete(Integer id) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.getEmployee().setUser(null);
        userRepo.delete(user);
    }

    @Override
    public User getById(Integer id) {
        return userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public List<User> getAll() {
        return userRepo.findAll();
    }
}
