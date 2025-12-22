package np.edu.nast.payroll.Payroll.service.impl;

import np.edu.nast.payroll.Payroll.entity.Attendance;
import np.edu.nast.payroll.Payroll.repository.AttendanceRepository;
import np.edu.nast.payroll.Payroll.service.AttendanceService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;

    public AttendanceServiceImpl(AttendanceRepository attendanceRepository) {
        this.attendanceRepository = attendanceRepository;
    }

    @Override
    public Attendance createAttendance(Attendance attendance) {
        return attendanceRepository.save(attendance);
    }

    @Override
    public Attendance updateAttendance(Long id, Attendance updated) {

        Attendance existing = attendanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Attendance not found with ID: " + id));

        existing.setCheckInTime(updated.getCheckInTime());
        existing.setCheckOutTime(updated.getCheckOutTime());
        existing.setAttendanceDate(updated.getAttendanceDate());
        existing.setInGpsLat(updated.getInGpsLat());
        existing.setInGpsLong(updated.getInGpsLong());
        existing.setEmployee(updated.getEmployee());

        return attendanceRepository.save(existing);
    }

    @Override
    public void deleteAttendance(Long id) {
        attendanceRepository.deleteById(id);
    }

    @Override
    public Attendance getAttendanceById(Long id) {
        return attendanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Attendance not found with ID: " + id));
    }

    @Override
    public List<Attendance> getAllAttendance() {
        return attendanceRepository.findAll();
    }

    @Override
    public List<Attendance> getAttendanceByEmployee(Long empId) {
        return attendanceRepository.findByEmployeeEmpId(empId);
    }
}
