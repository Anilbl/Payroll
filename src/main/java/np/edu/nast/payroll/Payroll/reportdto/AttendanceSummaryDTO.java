package np.edu.nast.payroll.Payroll.reportdto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AttendanceSummaryDTO {
    private long presentDays;
    private long absentDays;
    private long leaveDays;
}
