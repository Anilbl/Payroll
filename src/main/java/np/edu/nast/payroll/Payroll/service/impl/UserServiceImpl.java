package np.edu.nast.payroll.Payroll.service.impl;

import np.edu.nast.payroll.Payroll.entity.Employee;
import np.edu.nast.payroll.Payroll.entity.Role;
import np.edu.nast.payroll.Payroll.entity.User;
import np.edu.nast.payroll.Payroll.exception.ResourceNotFoundException;
import np.edu.nast.payroll.Payroll.exception.DuplicateResourceException;
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

    /* =========================
       CREATE USER
       ========================= */
    @Override
    public User create(User user) {

        if (user.getEmployee() == null || user.getEmployee().getEmpId() == null) {
            throw new IllegalArgumentException("Employee ID is required");
        }

        if (user.getRole() == null || user.getRole().getRoleId() == null) {
            throw new IllegalArgumentException("Role ID is required");
        }

        Employee employee = employeeRepo.findById(user.getEmployee().getEmpId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Employee not found with ID: " +
                                user.getEmployee().getEmpId())
                );

        // ONE EMPLOYEE → ONE USER (SERVICE-LEVEL ENFORCEMENT)
        if (employee.getUser() != null) {
            throw new DuplicateResourceException(
                    "User already exists for employee ID: " + employee.getEmpId()
            );
        }

        Role role = roleRepo.findById(user.getRole().getRoleId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Role not found with ID: " +
                                user.getRole().getRoleId())
                );

        user.setEmployee(employee);
        user.setEmail(employee.getEmail()); // email source of truth
        user.setRole(role);
        user.setStatus(user.getStatus() != null ? user.getStatus() : "ACTIVE");
        user.setCreatedAt(LocalDateTime.now());

        // Bidirectional link
        employee.setUser(user);

        return userRepo.save(user);
    }

    /* =========================
       UPDATE USER
       ========================= */
    @Override
    public User update(Integer id, User user) {

        User existing = userRepo.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found with ID: " + id)
                );

        existing.setUsername(user.getUsername());
        existing.setPassword(user.getPassword());

        if (user.getRole() != null && user.getRole().getRoleId() != null) {
            Role role = roleRepo.findById(user.getRole().getRoleId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Role not found with ID: " +
                                    user.getRole().getRoleId())
                    );
            existing.setRole(role);
        }

        if (user.getStatus() != null) {
            existing.setStatus(user.getStatus());
        }

        return userRepo.save(existing);
    }

    /* =========================
       DELETE USER
       ========================= */
    @Override
    public void delete(Integer id) {

        User user = userRepo.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found with ID: " + id)
                );

        // break relationship safely
        if (user.getEmployee() != null) {
            user.getEmployee().setUser(null);
        }

        userRepo.delete(user);
    }

    /* =========================
       READ OPERATIONS
       ========================= */
    @Override
    public User getById(Integer id) {
        return userRepo.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found with ID: " + id)
                );
    }

    @Override
    public List<User> getAll() {
        return userRepo.findAll();
    }
}
