package np.edu.nast.payroll.Payroll.repository;

import np.edu.nast.payroll.Payroll.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> { // Changed to Long

    // Matches Employee entity field 'empId'
    List<Attendance> findByEmployee_EmpId(Integer empId);

    // Used for the duplicate check logic
    boolean existsByEmployee_EmpIdAndAttendanceDate(Integer empId, LocalDate date);

    // Dashboard Statistics
    long countByAttendanceDateAndStatus(LocalDate date, String status);
    long countByAttendanceDate(LocalDate date);
    List<Attendance> findAllByAttendanceDate(LocalDate date);

    @Query("""
        SELECT 
            SUM(CASE WHEN a.status = 'PRESENT' THEN 1 ELSE 0 END),
            SUM(CASE WHEN a.status = 'ABSENT' THEN 1 ELSE 0 END),
            SUM(CASE WHEN a.status = 'LEAVE' THEN 1 ELSE 0 END)
        FROM Attendance a
        WHERE YEAR(a.attendanceDate) = :year AND MONTH(a.attendanceDate) = :month
    """)
    List<Object[]> summary(@Param("year") int year, @Param("month") int month);
}