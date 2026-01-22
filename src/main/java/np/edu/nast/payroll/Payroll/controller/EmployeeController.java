package np.edu.nast.payroll.Payroll.controller;

import np.edu.nast.payroll.Payroll.entity.Employee;
import np.edu.nast.payroll.Payroll.service.EmployeeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/employees")
@PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_ACCOUNTANT')")
public class EmployeeController {

    private final EmployeeService svc;

    public EmployeeController(EmployeeService svc) {
        this.svc = svc;
    }

    // --- ADMINISTRATIVE METHODS ---

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_ACCOUNTANT')")
    public ResponseEntity<Employee> create(@RequestBody Employee employee) {
        return new ResponseEntity<>(svc.create(employee), HttpStatus.CREATED);
    @ResponseStatus(HttpStatus.CREATED)
    public Employee create(@RequestBody Employee employee) {
        return svc.create(employee);
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_ACCOUNTANT')")
    public ResponseEntity<List<Employee>> getAll() {
        List<Employee> employees = svc.getAll();
        // Returning a 200 OK with the list helps the frontend handle empty states
        return ResponseEntity.ok(employees);
    public List<Employee> getAll() {
        return svc.getAll();
    }

    @GetMapping("/{id}")
    public Employee getById(@PathVariable Integer id) {
        return svc.getById(id);
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_ACCOUNTANT')")
    public ResponseEntity<Employee> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(svc.getById(id));
    }

    @PutMapping("/{id}")
    public Employee update(@PathVariable Integer id, @RequestBody Employee employee) {
        return svc.update(id, employee);
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_ACCOUNTANT')")
    public ResponseEntity<Employee> update(@PathVariable Integer id, @RequestBody Employee employee) {
        return ResponseEntity.ok(svc.update(id, employee));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        svc.delete(id);
    }

    // Active employee stats endpoint
    @GetMapping("/stats/active-per-month")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_ACCOUNTANT')")
    public ResponseEntity<Map<Integer, Long>> getActiveEmployeeStats() {
        return ResponseEntity.ok(svc.getActiveEmployeeStats());
    public Map<Integer, Long> getActiveEmployeeStats() {
        return svc.getActiveEmployeeStats();
    }

    // --- EMPLOYEE SELF-SERVICE METHODS ---

    @GetMapping("/profile/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_ACCOUNTANT', 'ROLE_EMPLOYEE')")
    public ResponseEntity<Employee> getProfile(@PathVariable Integer id) {
        return ResponseEntity.ok(svc.getById(id));
    }
}

    @PutMapping("/profile/update/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_ACCOUNTANT', 'ROLE_EMPLOYEE')")
    public ResponseEntity<Employee> updateProfile(@PathVariable Integer id, @RequestBody Employee employee) {
        return ResponseEntity.ok(svc.update(id, employee));
    }
}