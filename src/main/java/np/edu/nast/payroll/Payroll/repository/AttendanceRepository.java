package np.edu.nast.payroll.Payroll.repository;

<<<<<<< HEAD
import np.edu.nast.payroll.Payroll.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
=======
import jakarta.persistence.criteria.CriteriaBuilder;
import np.edu.nast.payroll.Payroll.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

>>>>>>> 3214be41b790e5d207ff8a4a5185d56a25676df5
import java.time.LocalDate;
import java.util.List;

public interface AttendanceRepository extends JpaRepository<Attendance, Integer> {

<<<<<<< HEAD
    // For duplicate check
    boolean existsByEmployee_EmpIdAndAttendanceDate(Integer empId, LocalDate date);

    // For employee history
    List<Attendance> findByEmployee_EmpId(Integer empId);

    // For Dashboard stats (Admin)
    long countByAttendanceDateAndStatus(LocalDate date, String status);

    // For Dashboard stats (Recent list)
    List<Attendance> findAllByAttendanceDate(LocalDate date);
=======

    // Fixes DashboardController
    long countByAttendanceDate(LocalDate date);
    List<Attendance> findAllByAttendanceDate(LocalDate date);

    // Fixes AttendanceServiceImpl
    List<Attendance> findByEmployee_EmpId(Integer empId);

    @Query("""
    SELECT
        SUM(CASE WHEN a.status='PRESENT' THEN 1 ELSE 0 END),
        SUM(CASE WHEN a.status='ABSENT' THEN 1 ELSE 0 END),
        SUM(CASE WHEN a.status='LEAVE' THEN 1 ELSE 0 END)
    FROM Attendance a
    WHERE YEAR(a.attendanceDate)=:year AND MONTH(a.attendanceDate)=:month
""")
    List<Object[]> summary(@Param("year") int year, @Param("month") int month);

>>>>>>> 3214be41b790e5d207ff8a4a5185d56a25676df5
}