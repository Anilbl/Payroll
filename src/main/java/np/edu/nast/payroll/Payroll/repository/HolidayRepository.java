package np.edu.nast.payroll.Payroll.repository;

import np.edu.nast.payroll.Payroll.entity.Holiday;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

public interface HolidayRepository extends JpaRepository<Holiday, Long> {
    @Query("SELECT COUNT(h) FROM Holiday h WHERE h.holidayDate BETWEEN :start AND :end")
    long countHolidaysInRange(@Param("start") LocalDate start, @Param("end") LocalDate end);


    // Crucial for payroll: Find holidays between two dates (month start and end)
    List<Holiday> findByHolidayDateBetween(LocalDate start, LocalDate end);

    // Check if a holiday already exists for a specific date to prevent duplicates
    boolean existsByHolidayDate(LocalDate date);
}