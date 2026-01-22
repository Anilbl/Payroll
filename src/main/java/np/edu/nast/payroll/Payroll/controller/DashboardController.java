package np.edu.nast.payroll.Payroll.controller;

import np.edu.nast.payroll.Payroll.entity.Attendance;
<<<<<<< HEAD
import np.edu.nast.payroll.Payroll.entity.LeaveBalance;
import np.edu.nast.payroll.Payroll.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
=======
import np.edu.nast.payroll.Payroll.repository.EmployeeRepository;
import np.edu.nast.payroll.Payroll.repository.EmployeeLeaveRepository;
import np.edu.nast.payroll.Payroll.repository.AttendanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
>>>>>>> 3214be41b790e5d207ff8a4a5185d56a25676df5
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "http://localhost:5173")
public class DashboardController {

<<<<<<< HEAD
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
=======
    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private EmployeeLeaveRepository leaveRepository;

    @Autowired
    private AttendanceRepository attendanceRepository;

    @GetMapping("/stats")
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        long totalEmployees = employeeRepository.count();
        long pendingLeaves = leaveRepository.countByStatus("PENDING");
        long presentToday = attendanceRepository.countByAttendanceDate(LocalDate.now());

        String attendancePercentage = totalEmployees > 0
                ? (presentToday * 100 / totalEmployees) + "%"
                : "0%";

        stats.put("totalWorkforce", totalEmployees);
        stats.put("leaveRequests", pendingLeaves);
        stats.put("dailyAttendance", attendancePercentage);
        return stats;
    }

    @GetMapping("/recent-attendance")
    public List<Attendance> getRecentAttendance() {
        return attendanceRepository.findAllByAttendanceDate(LocalDate.now());

>>>>>>> 3214be41b790e5d207ff8a4a5185d56a25676df5
    }
}