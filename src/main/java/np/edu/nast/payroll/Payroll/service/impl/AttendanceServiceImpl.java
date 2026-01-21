package np.edu.nast.payroll.Payroll.service.impl;

import lombok.RequiredArgsConstructor;
import np.edu.nast.payroll.Payroll.entity.Attendance;
import np.edu.nast.payroll.Payroll.entity.Employee;
import np.edu.nast.payroll.Payroll.exception.ResourceNotFoundException;
import np.edu.nast.payroll.Payroll.repository.AttendanceRepository;
import np.edu.nast.payroll.Payroll.repository.EmployeeRepository;
import np.edu.nast.payroll.Payroll.service.AttendanceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor // Automatically generates the constructor for final fields
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final EmployeeRepository employeeRepository;

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
        }

        return attendanceRepository.save(attendance);
    }

    @Override
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

        return attendanceRepository.save(existing);
    }

    @Override
    public List<Attendance> getAttendanceByEmployee(Integer empId) {
        return attendanceRepository.findByEmployee_EmpId(empId);
    }

    @Override
    public Attendance getAttendanceById(Integer id) {
        return attendanceRepository.findById(id).orElse(null);
    }

    @Override
    public List<Attendance> getAllAttendance() {
        return attendanceRepository.findAll();
    }

    @Override
    @Transactional
    public void deleteAttendance(Integer id) {
        if (!attendanceRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cannot delete. Attendance not found with ID: " + id);
        }
        attendanceRepository.deleteById(id);
    }
}