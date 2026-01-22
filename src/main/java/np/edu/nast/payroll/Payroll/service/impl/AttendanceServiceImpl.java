package np.edu.nast.payroll.Payroll.service.impl;

import np.edu.nast.payroll.Payroll.entity.Attendance;
import np.edu.nast.payroll.Payroll.repository.AttendanceRepository;
import np.edu.nast.payroll.Payroll.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AttendanceServiceImpl implements AttendanceService {

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Override
    public Attendance createAttendance(Attendance attendance) {
        // Prevent duplicate marking for the same day
        if (attendanceRepository.existsByEmployee_EmpIdAndAttendanceDate(
                attendance.getEmployee().getEmpId(),
                attendance.getAttendanceDate())) {
            throw new RuntimeException("Attendance already marked for this employee on this date.");
        }
        return attendanceRepository.save(attendance);
    }

    @Override
    public Attendance updateAttendance(Integer id, Attendance attendance) {
        // Repository uses Long, so we convert Integer id to Long
        Attendance existing = getAttendanceById(id);

        existing.setStatus(attendance.getStatus());

        // Match Entity fields: checkInTime and checkOutTime
        existing.setCheckInTime(attendance.getCheckInTime());
        existing.setCheckOutTime(attendance.getCheckOutTime());

        existing.setAttendanceDate(attendance.getAttendanceDate());
        existing.setWorkLocation(attendance.getWorkLocation());

        return attendanceRepository.save(existing);
    }

    @Override
    public void deleteAttendance(Integer id) {
        // Convert Integer to Long for Repository
        attendanceRepository.deleteById(id.longValue());
    }

    @Override
    public Attendance getAttendanceById(Integer id) {
        // Convert Integer to Long for Repository
        return attendanceRepository.findById(id.longValue()).orElseThrow(() ->
                new RuntimeException("Attendance not found with id: " + id));
    }

    @Override
    public List<Attendance> getAllAttendance() {
        return attendanceRepository.findAll();
    }

    @Override
    public List<Attendance> getAttendanceByEmployee(Integer empId) {
        return attendanceRepository.findByEmployee_EmpId(empId);
    }
}