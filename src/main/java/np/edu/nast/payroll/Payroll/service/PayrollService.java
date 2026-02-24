package np.edu.nast.payroll.Payroll.service;

import np.edu.nast.payroll.Payroll.dto.CommandCenterDTO;
import np.edu.nast.payroll.Payroll.dto.PayrollDashboardDTO;
import np.edu.nast.payroll.Payroll.entity.Payroll;
import np.edu.nast.payroll.Payroll.reportdto.PayrollSummaryDTO;

import java.util.List;
import java.util.Map;

public interface PayrollService {
    List<Payroll> getAllPayrolls();
    Payroll calculatePreview(Map<String, Object> payload);
    Payroll processPayroll(Map<String, Object> payload); // Stage 1
    void finalizePayroll(Integer payrollId, String transactionRef); // Stage 2
    void rollbackPayroll(Integer payrollId);
    List<Payroll> getPayrollByEmployeeId(Integer empId);
    Payroll updateStatus(Integer id, String status);
    Payroll voidPayroll(Integer id);
    Payroll getPayrollById(Integer id);
    List<PayrollDashboardDTO> getBatchCalculation(String month, int year);
    PayrollSummaryDTO getSalarySummary(int month, int year);
    CommandCenterDTO getCommandCenterData(int month, int year);
}