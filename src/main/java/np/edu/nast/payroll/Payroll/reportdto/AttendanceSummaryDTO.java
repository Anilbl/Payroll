package np.edu.nast.payroll.Payroll.reportdto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AttendanceSummaryDTO {
    // Basic Count Fields
    private long presentDays;
    private long absentDays;
    private long leaveDays;

    // Detailed Metric Fields
    private long lateArrivals;
    private double overtimeHours;
    private double overtimePay;
}