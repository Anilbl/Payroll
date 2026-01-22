package np.edu.nast.payroll.Payroll.controller;

import np.edu.nast.payroll.Payroll.entity.Attendance;
import np.edu.nast.payroll.Payroll.repository.AttendanceRepository;
import np.edu.nast.payroll.Payroll.repository.EmployeeLeaveRepository;
import np.edu.nast.payroll.Payroll.repository.EmployeeRepository;
import np.edu.nast.payroll.Payroll.repository.LeaveBalanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "http://localhost:5173")
public class DashboardController {

    @Autowired private EmployeeRepository employeeRepository;
    @Autowired private EmployeeLeaveRepository leaveRepository;
    @Autowired private AttendanceRepository attendanceRepository;
    @Autowired private LeaveBalanceRepository leaveBalanceRepository;

    /**
     * Aggregates key metrics for the Admin Dashboard.
     * Uses formatted strings for percentages and padding for UI alignment.
     */
    @GetMapping("/admin/stats")
    public Map<String, Object> getAdminDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        long totalEmployees = employeeRepository.count();

        // Count only 'Pending' leaves to alert admin of new requests
        long pendingLeaves = leaveRepository.countByStatus("Pending");

        // Count employees present specifically for today's date
        long presentToday = attendanceRepository.countByAttendanceDateAndStatus(LocalDate.now(), "PRESENT");

        double attendancePercentage = totalEmployees > 0
                ? (double) presentToday * 100 / totalEmployees
                : 0.0;

        stats.put("totalWorkforce", totalEmployees);
        stats.put("leaveRequests", String.format("%02d", pendingLeaves));
        stats.put("dailyAttendance", Math.round(attendancePercentage) + "%");
        return stats;
    }

    /**
     * Retrieves attendance list for today to display in the dashboard table.
     */
    @GetMapping("/recent-attendance")
    public List<Attendance> getRecentAttendance() {
        // Matches the method we verified in AttendanceRepository
        return attendanceRepository.findAllByAttendanceDate(LocalDate.now());
    }
}