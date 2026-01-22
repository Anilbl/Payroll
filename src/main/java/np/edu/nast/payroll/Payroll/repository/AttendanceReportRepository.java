package np.edu.nast.payroll.Payroll.repository;

import np.edu.nast.payroll.Payroll.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AttendanceReportRepository extends JpaRepository<Attendance, Long> {

    @Query("""
        SELECT COUNT(a) FROM Attendance a 
        WHERE a.status = :presentStatus 
          AND MONTH(a.attendanceDate) = :month 
          AND YEAR(a.attendanceDate) = :year
    """)
    long countByStatus(@Param("month") int month,
                       @Param("year") int year,
                       @Param("presentStatus") String status);

    @Query("""
        SELECT COUNT(a) FROM Attendance a 
        WHERE a.checkInTime IS NOT NULL 
          AND FUNCTION('HOUR', a.checkInTime) >= :startHour 
          AND FUNCTION('MINUTE', a.checkInTime) > :startMinute
          AND MONTH(a.attendanceDate) = :month 
          AND YEAR(a.attendanceDate) = :year
    """)
    long countLateArrivals(@Param("month") int month,
                           @Param("year") int year,
                           @Param("startHour") int hour,
                           @Param("startMinute") int minute);

    @Query("""
        SELECT COALESCE(SUM(
            CASE 
                WHEN FUNCTION('HOUR', a.checkOutTime) >= :endHour 
                THEN (FUNCTION('HOUR', a.checkOutTime) - :endHour) + (FUNCTION('MINUTE', a.checkOutTime) / 60.0)
                ELSE 0 
            END
        ), 0.0)
        FROM Attendance a
        WHERE a.checkOutTime IS NOT NULL
          AND MONTH(a.attendanceDate) = :month
          AND YEAR(a.attendanceDate) = :year
    """)
    double calculateTotalOvertime(@Param("month") int month,
                                  @Param("year") int year,
                                  @Param("endHour") int endHour);
}