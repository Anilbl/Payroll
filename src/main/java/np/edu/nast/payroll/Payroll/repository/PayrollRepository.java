package np.edu.nast.payroll.Payroll.repository;

import np.edu.nast.payroll.Payroll.entity.Payroll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PayrollRepository extends JpaRepository<Payroll, Integer> {

    // Fixes the Red Error lines in SalarySummaryService
    @Query("SELECT SUM(p.grossSalary) FROM Payroll p WHERE p.isVoided = false")
    Double sumTotalGross();

    @Query("SELECT SUM(p.totalDeductions) FROM Payroll p WHERE p.isVoided = false")
    Double sumTotalDeductions();

    @Query("SELECT SUM(p.netSalary) FROM Payroll p WHERE p.isVoided = false")
    Double sumTotalNet();

    long countByStatus(String status);

    // FIX: Returns only the LATEST record for each employee for the main dashboard
    @Query("SELECT p FROM Payroll p WHERE p.payrollId IN " +
            "(SELECT MAX(p2.payrollId) FROM Payroll p2 WHERE p2.isVoided = false GROUP BY p2.employee.empId)")
    List<Payroll> findLatestPayrollForEachEmployee();

    List<Payroll> findByEmployeeEmpIdOrderByPayDateDesc(Integer empId);
}