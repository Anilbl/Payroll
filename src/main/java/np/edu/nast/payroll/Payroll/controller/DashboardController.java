package np.edu.nast.payroll.Payroll.controller;

import np.edu.nast.payroll.Payroll.entity.Attendance;
import np.edu.nast.payroll.Payroll.entity.LeaveBalance;
import np.edu.nast.payroll.Payroll.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("/admin/stats")
    public Map<String, Object> getAdminDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        long totalEmployees = employeeRepository.count();

        // Matches the 'Pending' status logic
        long pendingLeaves = leaveRepository.countByStatus("Pending");

        // Fix: Method added to AttendanceRepository below
        long presentToday = attendanceRepository.countByAttendanceDateAndStatus(LocalDate.now(), "PRESENT");

        double attendancePercentage = totalEmployees > 0 ? (double) presentToday * 100 / totalEmployees : 0.0;

        stats.put("totalWorkforce", totalEmployees);
        stats.put("leaveRequests", String.format("%02d", pendingLeaves));
        stats.put("dailyAttendance", Math.round(attendancePercentage) + "%");
        return stats;
    }



    @GetMapping("/recent-attendance")
    public List<Attendance> getRecentAttendance() {
        // Fixes: "symbol: method findAllByAttendanceDate"
        return attendanceRepository.findAllByAttendanceDate(LocalDate.now());
    }
}