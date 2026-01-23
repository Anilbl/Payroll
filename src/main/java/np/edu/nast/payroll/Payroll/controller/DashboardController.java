package np.edu.nast.payroll.Payroll.controller;

import np.edu.nast.payroll.Payroll.entity.Attendance;
import np.edu.nast.payroll.Payroll.repository.EmployeeRepository;
import np.edu.nast.payroll.Payroll.repository.EmployeeLeaveRepository;
import np.edu.nast.payroll.Payroll.repository.AttendanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "http://localhost:5173")
public class DashboardController {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private EmployeeLeaveRepository leaveRepository;

    @Autowired
    private AttendanceRepository attendanceRepository;

    // Fixed mapping to match frontend request: /api/dashboard/admin/stats
    @GetMapping("/admin/stats")
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        try {
            long totalEmployees = employeeRepository.count();
            // Case-insensitive check to match your React status badge logic
            long pendingLeaves = leaveRepository.countByStatus("PENDING");
            long presentToday = attendanceRepository.countByAttendanceDate(LocalDate.now());

            // Calculation fix: Use double to ensure accuracy and handle zero workforce safely
            String attendancePercentage = "0%";
            if (totalEmployees > 0) {
                double percentage = ((double) presentToday / totalEmployees) * 100;
                attendancePercentage = Math.round(percentage) + "%";
            }

            stats.put("totalWorkforce", totalEmployees);
            stats.put("leaveRequests", pendingLeaves);
            stats.put("dailyAttendance", attendancePercentage);

        } catch (Exception e) {
            // Log the error and return empty/safe values to prevent 500 error
            e.printStackTrace();
            stats.put("totalWorkforce", 0);
            stats.put("leaveRequests", 0);
            stats.put("dailyAttendance", "0%");
        }
        return stats;
    }

    @GetMapping("/recent-attendance")
    public List<Attendance> getRecentAttendance() {
        try {
            List<Attendance> list = attendanceRepository.findAllByAttendanceDate(LocalDate.now());
            return (list != null) ? list : new ArrayList<>();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}