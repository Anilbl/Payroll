package np.edu.nast.payroll.Payroll.service;

import np.edu.nast.payroll.Payroll.entity.PayrollExtraComponent;
import java.util.List;
import java.util.Optional;

public interface PayrollExtraComponentService {
    List<PayrollExtraComponent> getComponentsByPayrollId(Integer payrollId);
    void deleteComponent(Long id);
    Optional<PayrollExtraComponent> getById(Long id);
    // You can add methods for specific reporting or adjustments here
}