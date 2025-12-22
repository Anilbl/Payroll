package np.edu.nast.payroll.Payroll.service.impl;

import np.edu.nast.payroll.Payroll.entity.Role;
import np.edu.nast.payroll.Payroll.entity.User;
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

    private final UserRepository repo;
    private final RoleRepository roleRepo;

    public UserServiceImpl(UserRepository repo, RoleRepository roleRepo) {
        this.repo = repo;
        this.roleRepo = roleRepo;
    }

    @Override
    public User create(User user) {
        // Fetch full Role from DB
        Role role = roleRepo.findById(user.getRole().getRoleId())
                .orElseThrow(() -> new RuntimeException("Role not found"));
        user.setRole(role);

        // Set default status and createdAt if null
        if (user.getStatus() == null) user.setStatus("ACTIVE");
        if (user.getCreatedAt() == null) user.setCreatedAt(LocalDateTime.now());

        return repo.save(user);
    }

    @Override
    public User update(Integer id, User user) {
        User existing = repo.findById(id).orElseThrow(() -> new RuntimeException("User not found"));

        existing.setUsername(user.getUsername());
        existing.setPassword(user.getPassword());
        existing.setEmail(user.getEmail());

        // Fetch role from DB to ensure roleName is populated
        if (user.getRole() != null && user.getRole().getRoleId() != null) {
            Role role = roleRepo.findById(user.getRole().getRoleId())
                    .orElseThrow(() -> new RuntimeException("Role not found"));
            existing.setRole(role);
        }

        if (user.getStatus() != null) existing.setStatus(user.getStatus());

        return repo.save(existing);
    }

    @Override
    public void delete(Integer id) {
        repo.deleteById(id);
    }

    @Override
    public User getById(Integer id) {
        return repo.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public List<User> getAll() {
        return repo.findAll();
    }
}
