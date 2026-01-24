package np.edu.nast.payroll.Payroll.controller;

import np.edu.nast.payroll.Payroll.repository.AttendanceRepository;
import np.edu.nast.payroll.Payroll.repository.EmployeeLeaveRepository;
import np.edu.nast.payroll.Payroll.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/employee/dashboard")
@CrossOrigin(origins = "http://localhost:5173")
public class EmployeeDashboardController {

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private EmployeeLeaveRepository leaveRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @GetMapping("/stats/{empId}")
    public Map<String, Object> getEmployeeDashboardStats(@PathVariable Integer empId) {
        Map<String, Object> stats = new HashMap<>();
        try {
            // 1. Calculate Monthly Attendance Percentage
            LocalDate now = LocalDate.now();
            int daysInMonth = now.lengthOfMonth();
            long daysPresent = attendanceRepository.countByEmployeeEmpIdAndAttendanceDateBetween(
                    empId,
                    now.withDayOfMonth(1),
                    now.withDayOfMonth(daysInMonth)
            );

            double percentage = ((double) daysPresent / daysInMonth) * 100;
            stats.put("attendance", Math.round(percentage * 10.0) / 10.0 + "%");

            // 2. Fetch Leave Balance (Pending/Approved total for current month)
            long leaveCount = leaveRepository.countByEmployeeEmpIdAndStatus(empId, "APPROVED");
            stats.put("leaveBalance", leaveCount + " Days");

            // 3. Placeholder for Net Salary (Can be linked to PayrollRepository later)
            stats.put("netSalary", "Rs. 0");

        } catch (Exception e) {
            e.printStackTrace();
            stats.put("attendance", "0.0%");
            stats.put("leaveBalance", "0 Days");
            stats.put("netSalary", "Rs. 0");
        }
        return stats;
    }
}