package np.edu.nast.payroll.Payroll.service.impl;

import lombok.RequiredArgsConstructor;
import np.edu.nast.payroll.Payroll.reportdto.AttendanceSummaryDTO;
import np.edu.nast.payroll.Payroll.repository.AttendanceReportRepository;
import np.edu.nast.payroll.Payroll.service.AttendanceReportService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AttendanceReportServiceImpl implements AttendanceReportService {

    private final AttendanceReportRepository repo;

    // These could be fetched from a database 'Settings' table in the future
    private static final String STATUS_PRESENT = "PRESENT";
    private static final String STATUS_ABSENT = "ABSENT";
    private static final String STATUS_LEAVE = "LEAVE";

    private static final int OFFICE_START_HOUR = 9;
    private static final int OFFICE_START_MINUTE = 15;
    private static final int OFFICE_END_HOUR = 17; // 5:00 PM
    private static final double OVERTIME_RATE = 150.0; // Rate per hour

    @Override
    public AttendanceSummaryDTO getAttendanceSummary(int month, int year) {

        long present = repo.countByStatus(month, year, STATUS_PRESENT);
        long absent = repo.countByStatus(month, year, STATUS_ABSENT);
        long leave = repo.countByStatus(month, year, STATUS_LEAVE);

        long late = repo.countLateArrivals(month, year, OFFICE_START_HOUR, OFFICE_START_MINUTE);

        double overtimeHours = repo.calculateTotalOvertime(month, year, OFFICE_END_HOUR);
        double overtimePay = overtimeHours * OVERTIME_RATE;

        return new AttendanceSummaryDTO(
                present,
                absent,
                leave,
                late,
                Math.round(overtimeHours * 100.0) / 100.0, // Rounded to 2 decimal places
                Math.round(overtimePay * 100.0) / 100.0
        );
    }
}