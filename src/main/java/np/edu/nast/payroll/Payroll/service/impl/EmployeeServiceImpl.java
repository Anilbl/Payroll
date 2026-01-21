package np.edu.nast.payroll.Payroll.service.impl;

import np.edu.nast.payroll.Payroll.dto.auth.DashboardStatsDTO;
import np.edu.nast.payroll.Payroll.entity.*;
import np.edu.nast.payroll.Payroll.exception.EmailAlreadyExistsException;
import np.edu.nast.payroll.Payroll.exception.ResourceNotFoundException;
import np.edu.nast.payroll.Payroll.repository.*;
import np.edu.nast.payroll.Payroll.service.EmployeeService;
import np.edu.nast.payroll.Payroll.service.EmailService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepo;
    private final DepartmentRepository departmentRepo;
    private final DesignationRepository designationRepo;
    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final EmailService emailService;
    private final AttendanceRepository attendanceRepo;
    private final PasswordEncoder passwordEncoder;

    public EmployeeServiceImpl(EmployeeRepository employeeRepo,
                               DepartmentRepository departmentRepo,
                               DesignationRepository designationRepo,
                               UserRepository userRepo,
                               RoleRepository roleRepo,
                               EmailService emailService,
                               AttendanceRepository attendanceRepo,
                               PasswordEncoder passwordEncoder) {
        this.employeeRepo = employeeRepo;
        this.departmentRepo = departmentRepo;
        this.designationRepo = designationRepo;
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.emailService = emailService;
        this.attendanceRepo = attendanceRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Employee create(Employee employee) {
        if (employeeRepo.existsByEmail(employee.getEmail())) {
            throw new EmailAlreadyExistsException("Employee email already exists: " + employee.getEmail());
        }

        Department dept = departmentRepo.findById(employee.getDepartment().getDeptId())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));
        Designation desig = designationRepo.findById(employee.getPosition().getDesignationId())
                .orElseThrow(() -> new ResourceNotFoundException("Designation not found"));

        employee.setDepartment(dept);
        employee.setPosition(desig);

        User user = userRepo.findByEmailIgnoreCase(employee.getEmail()).orElseGet(() -> {
            User newUser = new User();
            newUser.setEmail(employee.getEmail());
            newUser.setUsername(employee.getEmail().split("@")[0]);
            newUser.setPassword(passwordEncoder.encode("NAST123!"));
            newUser.setStatus("ACTIVE");

            Role employeeRole = roleRepo.findByRoleName("Employee")
                    .orElseThrow(() -> new ResourceNotFoundException("Role 'Employee' not found."));

            newUser.setRole(employeeRole);
            return userRepo.save(newUser);
        });

        employee.setUser(user);
        // Ensure new employees are active by default
        employee.setIsActive(true);
        Employee savedEmployee = employeeRepo.save(employee);

        user.setEmpId(savedEmployee.getEmpId());
        userRepo.save(user);

        return savedEmployee;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Employee> getAll() {
        return employeeRepo.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Employee getById(Integer id) {
        return employeeRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
    }

    @Override
    public Employee update(Integer id, Employee employee) {
        Employee existing = getById(id);
        existing.setFirstName(employee.getFirstName());
        existing.setLastName(employee.getLastName());
        existing.setContact(employee.getContact());
        existing.setAddress(employee.getAddress());
        existing.setEducation(employee.getEducation());
        existing.setMaritalStatus(employee.getMaritalStatus());
        existing.setEmploymentStatus(employee.getEmploymentStatus());
        existing.setBasicSalary(employee.getBasicSalary());

        // Use consistent naming: setIsActive
        existing.setIsActive(employee.getIsActive());

        if (employee.getDepartment() != null) {
            existing.setDepartment(departmentRepo.findById(employee.getDepartment().getDeptId())
                    .orElseThrow(() -> new ResourceNotFoundException("Dept not found")));
        }
        if (employee.getPosition() != null) {
            existing.setPosition(designationRepo.findById(employee.getPosition().getDesignationId())
                    .orElseThrow(() -> new ResourceNotFoundException("Designation not found")));
        }
        return employeeRepo.save(existing);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        // 1. Find the employee
        Employee employee = employeeRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));

        // 2. Perform Soft Delete by setting status to false
        // This solves the FK constraint error with bank_account
        employee.setIsActive(false);

        // 3. Deactivate the associated User account as well (Security Best Practice)
        if (employee.getUser() != null) {
            employee.getUser().setStatus("INACTIVE");
            userRepo.save(employee.getUser());
        }

        // 4. Save the update
        employeeRepo.save(employee);
    }

    @Override
    public DashboardStatsDTO getEmployeeStatsByUserId(Integer userId) {
        User user = userRepo.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Employee emp = employeeRepo.findByEmail(user.getEmail()).orElseThrow(() -> new ResourceNotFoundException("Profile not found"));

        DashboardStatsDTO dto = new DashboardStatsDTO();
        dto.setFirstName(emp.getFirstName());
        dto.setLastName(emp.getLastName());
        dto.setDesignation(emp.getPosition() != null ? emp.getPosition().getDesignationTitle() : "N/A");
        dto.setLastSalary(emp.getBasicSalary());
        return dto;
    }

    @Override
    public Map<Integer, Long> getActiveEmployeeStats() {
        List<Object[]> result = employeeRepo.countActiveEmployeesPerMonth();
        Map<Integer, Long> stats = new HashMap<>();
        for (Object[] row : result) {
            stats.put(((Number) row[0]).intValue(), ((Number) row[1]).longValue());
        }
        return stats;
    }
}