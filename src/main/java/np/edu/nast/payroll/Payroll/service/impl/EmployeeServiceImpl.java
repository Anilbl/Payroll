package np.edu.nast.payroll.Payroll.service.impl;

import np.edu.nast.payroll.Payroll.dto.auth.DashboardStatsDTO;
import np.edu.nast.payroll.Payroll.entity.*;
import np.edu.nast.payroll.Payroll.entity.Employee;
import np.edu.nast.payroll.Payroll.entity.Department;
import np.edu.nast.payroll.Payroll.entity.Designation;
import np.edu.nast.payroll.Payroll.exception.EmailAlreadyExistsException;
import np.edu.nast.payroll.Payroll.exception.ResourceNotFoundException;
import np.edu.nast.payroll.Payroll.repository.*;
import np.edu.nast.payroll.Payroll.repository.EmployeeRepository;
import np.edu.nast.payroll.Payroll.repository.DepartmentRepository;
import np.edu.nast.payroll.Payroll.repository.DesignationRepository;
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
                               DesignationRepository designationRepo) {
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
            throw new EmailAlreadyExistsException("Email already exists");
            throw new EmailAlreadyExistsException("Employee email already exists: " + employee.getEmail());
        }

        Department dept = departmentRepo.findById(employee.getDepartment().getDeptId())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));
        Designation desig = designationRepo.findById(employee.getPosition().getDesignationId())
                .orElseThrow(() -> new ResourceNotFoundException("Designation not found"));

        employee.setDepartment(dept);
        employee.setPosition(desig);
        // FK NULL CHECK
        if (employee.getDepartment() == null || employee.getDepartment().getDeptId() == null) {
            throw new IllegalArgumentException("Department ID is required");
        }
        if (employee.getPosition() == null || employee.getPosition().getDesignationId() == null) {
            throw new IllegalArgumentException("Designation ID is required");
        }

        User user = userRepo.findByEmailIgnoreCase(employee.getEmail()).orElseGet(() -> {
            User newUser = new User();
            newUser.setEmail(employee.getEmail());
            newUser.setUsername(employee.getEmail().split("@")[0]);
            newUser.setPassword(passwordEncoder.encode("NAST123!"));
            newUser.setStatus("ACTIVE");
        // FK EXISTENCE CHECK
        Department department = departmentRepo.findById(employee.getDepartment().getDeptId())
                .orElseThrow(() -> new RuntimeException(
                        "Department not found with id: " + employee.getDepartment().getDeptId()
                ));

            Role employeeRole = roleRepo.findByRoleName("Employee")
                    .orElseThrow(() -> new ResourceNotFoundException("Role 'Employee' not found."));
        Designation designation = designationRepo.findById(employee.getPosition().getDesignationId())
                .orElseThrow(() -> new RuntimeException(
                        "Designation not found with id: " + employee.getPosition().getDesignationId()
                ));

            newUser.setRole(employeeRole);
            return userRepo.save(newUser);
        });
        employee.setDepartment(department);
        employee.setPosition(designation);

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
        return employeeRepo.save(employee);
    }

    /* =========================
       UPDATE EMPLOYEE
       ========================= */
    @Override
    public Employee update(Integer id, Employee employee) {

        Employee existing = employeeRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));

        // EMAIL UNIQUENESS CHECK (ALLOW SAME EMPLOYEE)
        if (employee.getEmail() != null &&
                !employee.getEmail().equals(existing.getEmail()) &&
                employeeRepo.existsByEmail(employee.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists");
        }

        // FK NULL CHECK
        if (employee.getDepartment() == null || employee.getDepartment().getDeptId() == null ||
                employee.getPosition() == null || employee.getPosition().getDesignationId() == null) {
            throw new IllegalArgumentException("Department and Designation IDs are required");
        }

        // FK EXISTENCE CHECK
        Department department = departmentRepo.findById(employee.getDepartment().getDeptId())
                .orElseThrow(() -> new RuntimeException(
                        "Department not found with id: " + employee.getDepartment().getDeptId()
                ));

        Designation designation = designationRepo.findById(employee.getPosition().getDesignationId())
                .orElseThrow(() -> new RuntimeException(
                        "Designation not found with id: " + employee.getPosition().getDesignationId()
                ));

        // UPDATE VALUES
        existing.setDepartment(department);
        existing.setPosition(designation);
        Employee existing = getById(id);
        existing.setFirstName(employee.getFirstName());
        existing.setLastName(employee.getLastName());
        existing.setContact(employee.getContact());
        existing.setAddress(employee.getAddress());
        existing.setEducation(employee.getEducation());
        existing.setMaritalStatus(employee.getMaritalStatus());
        existing.setEducation(employee.getEducation());
        existing.setEmploymentStatus(employee.getEmploymentStatus());
        existing.setBasicSalary(employee.getBasicSalary());

        // Use consistent naming: setIsActive
        existing.setJoiningDate(employee.getJoiningDate());
        existing.setAddress(employee.getAddress());
        existing.setIsActive(employee.getIsActive());

        if (employee.getDepartment() != null) {
            existing.setDepartment(departmentRepo.findById(employee.getDepartment().getDeptId())
                    .orElseThrow(() -> new ResourceNotFoundException("Dept not found")));
        }
        if (employee.getPosition() != null) {
            existing.setPosition(designationRepo.findById(employee.getPosition().getDesignationId())
                    .orElseThrow(() -> new ResourceNotFoundException("Designation not found")));
        // EMAIL = SOURCE OF TRUTH
        if (employee.getEmail() != null) {
            existing.setEmail(employee.getEmail());

            if (existing.getUser() != null) {
                existing.getUser().setEmail(employee.getEmail());
            }
        }

        return employeeRepo.save(existing);
    }

    /* =========================
       DELETE EMPLOYEE
       ========================= */
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
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));
        employeeRepo.delete(employee);
    }

    /* =========================
       GET EMPLOYEE BY ID
       ========================= */
    @Override
    public Employee getById(Integer id) {
        return employeeRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));
    }

    /* =========================
       GET ALL EMPLOYEES
       ========================= */
    @Override
    public List<Employee> getAll() {
        return employeeRepo.findAll();
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

    /* =========================
       ACTIVE EMPLOYEE STATS
       ========================= */
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
