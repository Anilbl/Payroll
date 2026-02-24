package np.edu.nast.payroll.Payroll.repository;

import np.edu.nast.payroll.Payroll.entity.PayrollExtraComponent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PayrollExtraComponentRepository extends JpaRepository<PayrollExtraComponent, Long> {

    // Fetch all breakdown items for a specific payroll record
    List<PayrollExtraComponent> findByPayroll_PayrollId(Integer payrollId);

    // Fetch by component name (useful for specific allowance reporting)
    List<PayrollExtraComponent> findByComponentName(String componentName);

    // Fetch all earnings or all deductions for a specific payroll
    List<PayrollExtraComponent> findByPayroll_PayrollIdAndType(Integer payrollId, String type);
}