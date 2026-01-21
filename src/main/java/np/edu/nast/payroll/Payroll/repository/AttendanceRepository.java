package np.edu.nast.payroll.Payroll.repository;

import np.edu.nast.payroll.Payroll.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface AttendanceRepository extends JpaRepository<Attendance, Integer> {

    // For duplicate check
    boolean existsByEmployee_EmpIdAndAttendanceDate(Integer empId, LocalDate date);

    // For employee history
    List<Attendance> findByEmployee_EmpId(Integer empId);

    // For Dashboard stats (Admin)
    long countByAttendanceDateAndStatus(LocalDate date, String status);

    // For Dashboard stats (Recent list)
    List<Attendance> findAllByAttendanceDate(LocalDate date);
}