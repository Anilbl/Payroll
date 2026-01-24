package np.edu.nast.payroll.Payroll.service.impl;

import np.edu.nast.payroll.Payroll.entity.Attendance;
import np.edu.nast.payroll.Payroll.entity.Employee;
import np.edu.nast.payroll.Payroll.exception.ResourceNotFoundException;
import np.edu.nast.payroll.Payroll.repository.AttendanceRepository;
import np.edu.nast.payroll.Payroll.repository.EmployeeRepository;
import np.edu.nast.payroll.Payroll.service.AttendanceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final EmployeeRepository employeeRepository;

    public AttendanceServiceImpl(AttendanceRepository attendanceRepository, EmployeeRepository employeeRepository) {
        this.attendanceRepository = attendanceRepository;
        this.employeeRepository = employeeRepository;
    }

    @Override
    @Transactional
    public Attendance createAttendance(Attendance attendance) {
        // PREVIOUS LOGIC: Check if employee object or ID is null BEFORE calling repository
        if (attendance.getEmployee() == null || attendance.getEmployee().getEmpId() == null) {
            throw new IllegalArgumentException("Employee identity is missing. Please re-login.");
        }

        Integer empId = attendance.getEmployee().getEmpId();
        Employee employee = employeeRepository.findById(empId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee record not found for ID: " + empId));

        // PREVIOUS LOGIC: 10-HOUR RULE
        Optional<Attendance> lastRecord = attendanceRepository.findTopByEmployee_EmpIdOrderByAttendanceIdDesc(empId);
        if (lastRecord.isPresent()) {
            LocalDateTime lastCheckIn = lastRecord.get().getCheckInTime();
            long hoursSinceLastIn = Duration.between(lastCheckIn, LocalDateTime.now()).toHours();
            if (hoursSinceLastIn < 10) {
                throw new IllegalArgumentException("10-Hour Rule: You can only check in once every 10 hours.");
            }
        }

        // PREVIOUS LOGIC: SET SERVER TIME (Ignores incorrect frontend time)
        attendance.setEmployee(employee);
        attendance.setCheckInTime(LocalDateTime.now());
        attendance.setAttendanceDate(LocalDate.now());
        attendance.setStatus("PRESENT");

        return attendanceRepository.save(attendance);
    }

    @Override
    @Transactional
    public Attendance updateAttendance(Integer id, Attendance updated) {
        Attendance existing = attendanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance record " + id + " not found"));

        // PREVIOUS LOGIC: If this is a checkout, use server time
        if (updated.getCheckOutTime() != null || "Checked Out".equals(updated.getStatus())) {
            existing.setCheckOutTime(LocalDateTime.now());
            existing.setStatus("COMPLETED"); // Ensures status updates from PRESENT to COMPLETED
        }

        return attendanceRepository.save(existing);
    }

    @Override
    @Transactional // Added Transactional here because it performs database updates
    public List<Attendance> getAttendanceByEmployee(Integer empId) {
        // PREVIOUS LOGIC: Return empty list if empId is null
        if (empId == null) return List.of();

        List<Attendance> records = attendanceRepository.findByEmployee_EmpId(empId);

        // PREVIOUS LOGIC: 8-HOUR AUTO-CHECKOUT (Applied when fetching history)
        return records.stream().map(record -> {
            if (record.getCheckOutTime() == null) {
                long hoursActive = Duration.between(record.getCheckInTime(), LocalDateTime.now()).toHours();
                if (hoursActive >= 8) {
                    record.setCheckOutTime(record.getCheckInTime().plusHours(8));
                    record.setStatus("AUTO-CHECKOUT"); // New: Mark as auto-checked out for clarity
                    attendanceRepository.save(record);
                }
            }
            return record;
        }).collect(Collectors.toList());
    }

    @Override public void deleteAttendance(Integer id) { attendanceRepository.deleteById(id); }
    @Override public Attendance getAttendanceById(Integer id) { return attendanceRepository.findById(id).orElse(null); }
    @Override public List<Attendance> getAllAttendance() { return attendanceRepository.findAll(); }
}