package np.edu.nast.payroll.Payroll.repository;

import np.edu.nast.payroll.Payroll.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Integer> {

    // PREVIOUS: Inclusive between (Start <= date <= End)
    List<Attendance> findByEmployee_EmpIdAndAttendanceDateBetween(Integer empId, LocalDate start, LocalDate end);

    // NEW: Accurate Monthly Boundary (Start <= date < End)
    // This ensures if End is March 1, March 1 records are NOT included.
    List<Attendance> findByEmployee_EmpIdAndAttendanceDateGreaterThanEqualAndAttendanceDateLessThan(Integer empId, LocalDate start, LocalDate end);

    long countByAttendanceDate(LocalDate date);
    List<Attendance> findAllByAttendanceDate(LocalDate date);
    List<Attendance> findByEmployee_EmpId(Integer empId);

    Optional<Attendance> findTopByEmployee_EmpIdOrderByAttendanceIdDesc(Integer empId);

    long countByEmployeeEmpIdAndAttendanceDateBetween(Integer empId, LocalDate start, LocalDate end);

    @Query("""
    SELECT 
        SUM(CASE WHEN a.status='PRESENT' THEN 1 ELSE 0 END),
        SUM(CASE WHEN a.status='ABSENT' THEN 1 ELSE 0 END),
        SUM(CASE WHEN a.status='LEAVE' THEN 1 ELSE 0 END)
    FROM Attendance a 
    WHERE YEAR(a.attendanceDate)=:year AND MONTH(a.attendanceDate)=:month
    """)
    List<Object[]> summary(@Param("year") int year, @Param("month") int month);
}