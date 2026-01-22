package np.edu.nast.payroll.Payroll.repository;

import np.edu.nast.payroll.Payroll.entity.Employee;
<<<<<<< HEAD
import np.edu.nast.payroll.Payroll.reportdto.MonthlyPayrollDTO;
=======
import np.edu.nast.payroll.Payroll.entity.Report;
>>>>>>> 3214be41b790e5d207ff8a4a5185d56a25676df5
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
<<<<<<< HEAD
public interface ReportRepository extends JpaRepository<Employee, String> {

    @Query("SELECT COUNT(e) FROM Employee e")
    long countEmployees();

    @Query("""
        SELECT COALESCE(SUM(p.netSalary), 0)
        FROM Payroll p
        WHERE YEAR(p.payDate) = :year
    """)
    double sumPayroll(int year);

    @Query("""
        SELECT COALESCE(SUM(p.totalDeductions), 0)
        FROM Payroll p
        WHERE YEAR(p.payDate) = :year
    """)
    double sumDeductions(int year);

    @Query("""
        SELECT COALESCE(SUM(p.totalAllowances), 0)
        FROM Payroll p
        WHERE YEAR(p.payDate) = :year
    """)
    double sumAllowances(int year);

    @Query("""
        SELECT new np.edu.nast.payroll.Payroll.reportdto.MonthlyPayrollDTO(
            FUNCTION('MONTHNAME', p.payDate),
            SUM(p.netSalary)
        )
        FROM Payroll p
        WHERE YEAR(p.payDate) = :year
        GROUP BY MONTH(p.payDate), FUNCTION('MONTHNAME', p.payDate)
        ORDER BY MONTH(p.payDate)
    """)
    List<MonthlyPayrollDTO> monthlyPayroll(int year);
}
=======

public interface ReportRepository extends JpaRepository<Report, Long> {



}
>>>>>>> 3214be41b790e5d207ff8a4a5185d56a25676df5
