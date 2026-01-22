package np.edu.nast.payroll.Payroll.controller;

import np.edu.nast.payroll.Payroll.entity.Attendance;
import np.edu.nast.payroll.Payroll.service.AttendanceService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/attendance")
<<<<<<< HEAD
@CrossOrigin(origins = "http://localhost:5173")
=======
@CrossOrigin(origins = "http://localhost:5173") // Matches your frontend port
>>>>>>> 3214be41b790e5d207ff8a4a5185d56a25676df5
public class AttendanceController {

    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @PostMapping
    public Attendance create(@RequestBody Attendance attendance) {
        return attendanceService.createAttendance(attendance);
    }

    @PutMapping("/{id}")
<<<<<<< HEAD
    public Attendance update(@PathVariable Integer id, @RequestBody Attendance attendance) {
        return attendanceService.updateAttendance(id, attendance);
=======
    public Attendance update(@PathVariable Long id, @RequestBody Attendance attendance) {
        // Ensuring ID conversion to match your service's expected type
        return attendanceService.updateAttendance(id.intValue(), attendance);
>>>>>>> 3214be41b790e5d207ff8a4a5185d56a25676df5
    }

    @GetMapping("/employee/{empId}")
    public List<Attendance> getByEmployee(@PathVariable Integer empId) {
        return attendanceService.getAttendanceByEmployee(empId);
    }
<<<<<<< HEAD
=======

>>>>>>> 3214be41b790e5d207ff8a4a5185d56a25676df5
}