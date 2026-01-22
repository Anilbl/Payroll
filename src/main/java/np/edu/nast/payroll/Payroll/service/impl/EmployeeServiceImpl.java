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

    /* =========================
       CREATE EMPLOYEE
       ========================= */
    @Override
    public Employee create(Employee employee) {
        // EMAIL UNIQUENESS CHECK
        if (employeeRepo.existsByEmail(employee.getEmail())) {
            throw new EmailAlreadyExistsException("Employee email already exists: " + employee.getEmail());
        }

        // FETCH AND VALIDATE DEPARTMENT/DESIGNATION
        Department department = departmentRepo.findById(employee.getDepartment().getDeptId())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + employee.getDepartment().getDeptId()));

        Designation designation = designationRepo.findById(employee.getPosition().getDesignationId())
                .orElseThrow(() -> new ResourceNotFoundException("Designation not found with id: " + employee.getPosition().getDesignationId()));

        employee.setDepartment(department);
        employee.setPosition(designation);

        // CREATE OR LINK USER ACCOUNT
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
        employee.setIsActive(true);
        Employee savedEmployee = employeeRepo.save(employee);

        // Link User back to Employee ID
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

    /* =========================
       UPDATE EMPLOYEE
       ========================= */
    @Override
    public Employee update(Integer id, Employee employee) {
        Employee existing = getById(id);

        // EMAIL UNIQUENESS CHECK (ALLOW SAME EMPLOYEE)
        if (employee.getEmail() != null && !employee.getEmail().equalsIgnoreCase(existing.getEmail()) &&
                employeeRepo.existsByEmail(employee.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists: " + employee.getEmail());
        }

        // UPDATE BASIC INFO
        existing.setFirstName(employee.getFirstName());
        existing.setLastName(employee.getLastName());
        existing.setContact(employee.getContact());
        existing.setAddress(employee.getAddress());
        existing.setEducation(employee.getEducation());
        existing.setMaritalStatus(employee.getMaritalStatus());
        existing.setEmploymentStatus(employee.getEmploymentStatus());
        existing.setBasicSalary(employee.getBasicSalary());
        existing.setJoiningDate(employee.getJoiningDate());
        existing.setIsActive(employee.getIsActive());

        // UPDATE RELATIONS
        if (employee.getDepartment() != null) {
            existing.setDepartment(departmentRepo.findById(employee.getDepartment().getDeptId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department not found")));
        }
        if (employee.getPosition() != null) {
            existing.setPosition(designationRepo.findById(employee.getPosition().getDesignationId())
                    .orElseThrow(() -> new ResourceNotFoundException("Designation not found")));
        }

        // UPDATE EMAIL IN BOTH PLACES
        if (employee.getEmail() != null) {
            existing.setEmail(employee.getEmail());
            if (existing.getUser() != null) {
                existing.getUser().setEmail(employee.getEmail());
            }
        }

        return employeeRepo.save(existing);
    }

    /* =========================
       DELETE EMPLOYEE (SOFT DELETE)
       ========================= */
    @Override
    @Transactional
    public void delete(Integer id) {
        Employee employee = employeeRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));

        // Soft Delete to preserve data integrity for Payroll/Attendance
        employee.setIsActive(false);

        if (employee.getUser() != null) {
            employee.getUser().setStatus("INACTIVE");
            userRepo.save(employee.getUser());
        }

        employeeRepo.save(employee);
    }

    /* =========================
       DASHBOARD & ANALYTICS
       ========================= */
    @Override
    @Transactional(readOnly = true)
    public DashboardStatsDTO getEmployeeStatsByUserId(Integer userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Employee emp = employeeRepo.findByEmail(user.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));

        DashboardStatsDTO dto = new DashboardStatsDTO();
        dto.setFirstName(emp.getFirstName());
        dto.setLastName(emp.getLastName());
        dto.setDesignation(emp.getPosition() != null ? emp.getPosition().getDesignationTitle() : "N/A");
        dto.setLastSalary(emp.getBasicSalary());
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<Integer, Long> getActiveEmployeeStats() {
        List<Object[]> result = employeeRepo.countActiveEmployeesPerMonth();
        Map<Integer, Long> stats = new HashMap<>();
        for (Object[] row : result) {
            stats.put(((Number) row[0]).intValue(), ((Number) row[1]).longValue());
        }
        return stats;
    }
}