package np.edu.nast.payroll.Payroll.service;

import np.edu.nast.payroll.Payroll.dto.auth.PayrollRequest;
import np.edu.nast.payroll.Payroll.entity.Payroll;
import java.util.List;
import java.util.Map;

public interface PayrollService {
    // Main dashboard: current month only
    List<Payroll> getAllPayrolls();

    // One-click history: all 12 months for one employee
    List<Payroll> getEmployeeHistory(Integer empId);

    Payroll voidPayroll(Integer id, String remarks);
    Payroll processPayrollRequest(PayrollRequest request);
    // This fixes the red error in PayrollController
    Payroll processPayroll(Map<String, Object> payload);
    Payroll updateStatus(Integer id, String status);
    Payroll savePayroll(Payroll p);
    Payroll getPayrollById(Integer id);
    void deletePayroll(Integer id);
}