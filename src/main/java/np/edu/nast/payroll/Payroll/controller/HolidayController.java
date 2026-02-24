package np.edu.nast.payroll.Payroll.controller;

import lombok.RequiredArgsConstructor;
import np.edu.nast.payroll.Payroll.entity.Holiday;
import np.edu.nast.payroll.Payroll.service.HolidayService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/holidays")
@RequiredArgsConstructor
public class HolidayController {

    private final HolidayService holidayService;

    @PostMapping("/sync/{year}")
    public ResponseEntity<String> syncNationalHolidays(@PathVariable int year) {
        holidayService.syncNationalHolidays(year);
        return ResponseEntity.ok("Successfully synchronized Nepal National Holidays for " + year);
    }

    @PostMapping("/generate-saturdays")
    public ResponseEntity<String> generateSaturdays(
            @RequestParam("year") int year,
            @RequestParam("month") int month) {
        holidayService.generateSaturdaysForMonth(year, month);
        return ResponseEntity.ok("Saturdays for " + year + "-" + month + " generated successfully.");
    }

    @PostMapping
    public ResponseEntity<Holiday> createHoliday(@RequestBody Holiday holiday) {
        return ResponseEntity.ok(holidayService.saveHoliday(holiday));
    }

    /**
     * BULK CREATE HOLIDAYS
     * Supports multi-day selection (e.g., Dashain vacation)
     */
    @PostMapping("/bulk")
    public ResponseEntity<String> createHolidayRange(
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            @RequestParam("description") String description,
            @RequestParam(value = "type", defaultValue = "NATIONAL") String type) {
        holidayService.saveHolidayRange(start, end, description, type);
        return ResponseEntity.ok("Holiday range created successfully.");
    }

    @GetMapping("/filter")
    public ResponseEntity<List<Holiday>> getHolidays(
            @RequestParam(name = "start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(name = "end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return ResponseEntity.ok(holidayService.getHolidaysInRange(start, end));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeHoliday(@PathVariable Long id) {
        holidayService.deleteHoliday(id);
        return ResponseEntity.noContent().build();
    }
}