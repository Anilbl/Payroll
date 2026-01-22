package np.edu.nast.payroll.Payroll.repository;

import np.edu.nast.payroll.Payroll.entity.Payroll;
import np.edu.nast.payroll.Payroll.reportdto.MonthlyPayrollDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PayrollRepository extends JpaRepository<Payroll, Integer> {

    // --- Your Local Changes (Salary Summary & Dashboard Fixes) ---

    @Query("SELECT SUM(p.grossSalary) FROM Payroll p WHERE p.isVoided = false")
    Double sumTotalGross();

    @Query("SELECT SUM(p.totalDeductions) FROM Payroll p WHERE p.isVoided = false")
    Double sumTotalDeductions();

    @Query("SELECT SUM(p.netSalary) FROM Payroll p WHERE p.isVoided = false")
    Double sumTotalNet();

    long countByStatus(String status);

    @Query("SELECT p FROM Payroll p WHERE p.payrollId IN " +
            "(SELECT MAX(p2.payrollId) FROM Payroll p2 WHERE p2.isVoided = false GROUP BY p2.employee.empId)")
    List<Payroll> findLatestPayrollForEachEmployee();

    List<Payroll> findByEmployeeEmpIdOrderByPayDateDesc(Integer empId);


    // --- Server Changes (Yearly & Monthly Reporting Logic) ---

    @Query("""
        SELECT COALESCE(SUM(p.netSalary),0)
        FROM Payroll p
        WHERE YEAR(p.payDate)=:year
    """)
    double yearlyPayroll(@Param("year") int year);

    @Query("""
        SELECT COALESCE(SUM(p.totalDeductions),0)
        FROM Payroll p
        WHERE YEAR(p.payDate)=:year
    """)
    double yearlyDeductions(@Param("year") int year);

    @Query("""
        SELECT COALESCE(SUM(p.totalAllowances),0)
        FROM Payroll p
        WHERE YEAR(p.payDate)=:year
    """)
    double yearlyAllowances(@Param("year") int year);

    @Query("""
        SELECT new np.edu.nast.payroll.Payroll.reportdto.MonthlyPayrollDTO(
            FUNCTION('MONTHNAME', p.payDate), SUM(p.netSalary)
        )
        FROM Payroll p
        WHERE FUNCTION('YEAR', p.payDate) = :year
        GROUP BY FUNCTION('MONTH', p.payDate), FUNCTION('MONTHNAME', p.payDate)
        ORDER BY FUNCTION('MONTH', p.payDate)
    """)
    List<MonthlyPayrollDTO> monthlyPayroll(@Param("year") int year);
}