package np.edu.nast.payroll.Payroll.reportdto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AttendanceSummaryDTO {
<<<<<<< HEAD
    private long totalPresent;
    private long totalAbsent;
    private long lateArrivals;
    private double overtimeHours;
    private double overtimePay;
=======
    private long presentDays;
    private long absentDays;
    private long leaveDays;
>>>>>>> 3214be41b790e5d207ff8a4a5185d56a25676df5
}
