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

    @PostMapping
    public Attendance create(@RequestBody Attendance attendance) {
        return attendanceService.createAttendance(attendance);
    }

    // Specific endpoint for Employee Panel Check-out
    @PutMapping("/checkout/{id}")
    public Attendance checkOut(@PathVariable Integer id) {
        Attendance attendance = new Attendance();
        attendance.setCheckOutTime(java.time.LocalDateTime.now());
        return attendanceService.updateAttendance(id, attendance);
    }

    @PutMapping("/{id}")
    public Attendance update(@PathVariable Integer id, @RequestBody Attendance attendance) {
        return attendanceService.updateAttendance(id, attendance);
    }

    @GetMapping("/employee/{empId}")
    public List<Attendance> getByEmployee(@PathVariable Integer empId) {
        return attendanceService.getAttendanceByEmployee(empId);
    }
}