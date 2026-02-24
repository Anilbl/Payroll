package np.edu.nast.payroll.Payroll.service;

import np.edu.nast.payroll.Payroll.entity.Holiday;
import java.time.LocalDate;
import java.util.List;

public interface HolidayService {
    // Basic CRUD
    Holiday saveHoliday(Holiday holiday);
    List<Holiday> getHolidaysInRange(LocalDate start, LocalDate end);
    void deleteHoliday(Long id);

    // Automation & Multi-day
    void syncNationalHolidays(int year);
    void generateSaturdaysForMonth(int year, int month);

    // New: Handle range selection from frontend
    void saveHolidayRange(LocalDate start, LocalDate end, String description, String type);
}