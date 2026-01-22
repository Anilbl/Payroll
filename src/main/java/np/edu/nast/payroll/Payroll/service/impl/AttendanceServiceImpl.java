package np.edu.nast.payroll.Payroll.service.impl;

<<<<<<< HEAD
import lombok.RequiredArgsConstructor;
=======
>>>>>>> 3214be41b790e5d207ff8a4a5185d56a25676df5
import np.edu.nast.payroll.Payroll.entity.Attendance;
import np.edu.nast.payroll.Payroll.entity.Employee;
import np.edu.nast.payroll.Payroll.exception.ResourceNotFoundException;
import np.edu.nast.payroll.Payroll.repository.AttendanceRepository;
import np.edu.nast.payroll.Payroll.repository.EmployeeRepository;
import np.edu.nast.payroll.Payroll.service.AttendanceService;
import org.springframework.stereotype.Service;
<<<<<<< HEAD
import org.springframework.transaction.annotation.Transactional;
=======
>>>>>>> 3214be41b790e5d207ff8a4a5185d56a25676df5

import java.time.LocalDate;
import java.util.List;

@Service
<<<<<<< HEAD
@RequiredArgsConstructor // Automatically generates the constructor for final fields
=======
>>>>>>> 3214be41b790e5d207ff8a4a5185d56a25676df5
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final EmployeeRepository employeeRepository;

<<<<<<< HEAD
    @Override
    @Transactional
    public Attendance createAttendance(Attendance attendance) {
        // 1. Validate Employee
        if (attendance.getEmployee() == null || attendance.getEmployee().getEmpId() == null) {
            throw new IllegalArgumentException("Employee ID is required");
        }

        Employee employee = employeeRepository.findById(attendance.getEmployee().getEmpId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + attendance.getEmployee().getEmpId()));

        // 2. Set Date (Default to today if null)
        LocalDate date = (attendance.getAttendanceDate() != null) ? attendance.getAttendanceDate() : LocalDate.now();
        attendance.setAttendanceDate(date);

        // 3. Prevent Duplicates (One record per employee per day)
        boolean exists = attendanceRepository.existsByEmployee_EmpIdAndAttendanceDate(employee.getEmpId(), date);
        if (exists) {
            throw new IllegalStateException("Attendance already recorded for this employee on " + date);
        }

        // 4. Set Status and Link Employee
        attendance.setEmployee(employee);
        if (attendance.getStatus() == null || attendance.getStatus().isBlank()) {
            attendance.setStatus(attendance.getCheckInTime() != null ? "PRESENT" : "ABSENT");
        } else {
            attendance.setStatus(attendance.getStatus().toUpperCase());
=======
    public AttendanceServiceImpl(AttendanceRepository attendanceRepository,
                                 EmployeeRepository employeeRepository) {
        this.attendanceRepository = attendanceRepository;
        this.employeeRepository = employeeRepository;
    }

    @Override
    public Attendance createAttendance(Attendance attendance) {
        // FK null check
        if (attendance.getEmployee() == null || attendance.getEmployee().getEmpId() == null) {
            throw new IllegalArgumentException("Employee ID is required for attendance");
        }

        // Check if employee exists
        Employee employee = employeeRepository.findById(attendance.getEmployee().getEmpId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Employee not found with ID: " + attendance.getEmployee().getEmpId()));

        attendance.setEmployee(employee);

        // ✅ SET DATE
        if (attendance.getAttendanceDate() == null) {
            attendance.setAttendanceDate(LocalDate.now());
        }

        // ✅ SET STATUS (THIS WAS MISSING)
        if (attendance.getStatus() == null || attendance.getStatus().isBlank()) {
            // Default logic if no status is provided
            if (attendance.getCheckInTime() != null) {
                attendance.setStatus("PRESENT");
            } else {
                attendance.setStatus("ABSENT");
            }
        } else {
            // Normalize status to uppercase for consistency
            attendance.setStatus(attendance.getStatus().toUpperCase()); // e.g., "LEAVE"
>>>>>>> 3214be41b790e5d207ff8a4a5185d56a25676df5
        }

        return attendanceRepository.save(attendance);
    }

    @Override
<<<<<<< HEAD
    @Transactional
    public Attendance updateAttendance(Integer id, Attendance updated) {
        Attendance existing = attendanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance record not found with ID: " + id));

        // Update Check-Out Time and Status
        if (updated.getCheckOutTime() != null) {
            existing.setCheckOutTime(updated.getCheckOutTime());
        }

        if (updated.getStatus() != null) {
            existing.setStatus(updated.getStatus().toUpperCase());
        }

=======
    public Attendance updateAttendance(Integer id, Attendance updated) {
        Attendance existing = attendanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance not found with ID: " + id));

        if (updated.getEmployee() == null || updated.getEmployee().getEmpId() == null) {
            throw new IllegalArgumentException("Employee ID is required for attendance update");
        }

        Employee employee = employeeRepository.findById(updated.getEmployee().getEmpId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Employee not found with ID: " + updated.getEmployee().getEmpId()));

        existing.setCheckInTime(updated.getCheckInTime());
        existing.setCheckOutTime(updated.getCheckOutTime());
        existing.setAttendanceDate(updated.getAttendanceDate());
        existing.setInGpsLat(updated.getInGpsLat());
        existing.setInGpsLong(updated.getInGpsLong());
        existing.setEmployee(employee);

        if (updated.getStatus() != null && !updated.getStatus().isBlank()) {
            existing.setStatus(updated.getStatus().toUpperCase());
        }
>>>>>>> 3214be41b790e5d207ff8a4a5185d56a25676df5
        return attendanceRepository.save(existing);
    }

    @Override
<<<<<<< HEAD
    public List<Attendance> getAttendanceByEmployee(Integer empId) {
        return attendanceRepository.findByEmployee_EmpId(empId);
=======
    public void deleteAttendance(Integer id) {
        if (!attendanceRepository.existsById(id)) {
            throw new ResourceNotFoundException("Attendance not found with ID: " + id);
        }
        attendanceRepository.deleteById(id);
>>>>>>> 3214be41b790e5d207ff8a4a5185d56a25676df5
    }

    @Override
    public Attendance getAttendanceById(Integer id) {
<<<<<<< HEAD
        return attendanceRepository.findById(id).orElse(null);
=======
        return attendanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance not found with ID: " + id));
>>>>>>> 3214be41b790e5d207ff8a4a5185d56a25676df5
    }

    @Override
    public List<Attendance> getAllAttendance() {
        return attendanceRepository.findAll();
    }

    @Override
<<<<<<< HEAD
    @Transactional
    public void deleteAttendance(Integer id) {
        if (!attendanceRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cannot delete. Attendance not found with ID: " + id);
        }
        attendanceRepository.deleteById(id);
    }
}
=======
    public List<Attendance> getAttendanceByEmployee(Integer empId) {
        // Validate employee existence
        Employee employee = employeeRepository.findById(empId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + empId));

        return attendanceRepository.findByEmployee_EmpId(employee.getEmpId());


    }




}
>>>>>>> 3214be41b790e5d207ff8a4a5185d56a25676df5
