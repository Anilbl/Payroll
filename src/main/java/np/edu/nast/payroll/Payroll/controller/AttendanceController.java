package np.edu.nast.payroll.Payroll.controller;

import np.edu.nast.payroll.Payroll.entity.Attendance;
import np.edu.nast.payroll.Payroll.service.AttendanceService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/attendance")
@CrossOrigin(origins = "http://localhost:5173")
public class AttendanceController {

    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    /**
     * Records a new attendance entry (Check-in).
     */
    @PostMapping
    public Attendance create(@RequestBody Attendance attendance) {
        return attendanceService.createAttendance(attendance);
    }

    /**
     * Updates an attendance entry (e.g., Check-out).
     * Standardized to Integer ID to match the project schema.
     */
    @PutMapping("/{id}")
    public Attendance update(@PathVariable Integer id, @RequestBody Attendance attendance) {
        return attendanceService.updateAttendance(id, attendance);
    }

    /**
     * Retrieves all attendance records for a specific employee.
     */
    @GetMapping("/employee/{empId}")
    public List<Attendance> getByEmployee(@PathVariable Integer empId) {
        return attendanceService.getAttendanceByEmployee(empId);
    }
}