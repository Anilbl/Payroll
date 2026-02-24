package np.edu.nast.payroll.Payroll.repository;
import jakarta.transaction.Transactional;
import np.edu.nast.payroll.Payroll.entity.PayoutInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PayoutInfoRepository extends JpaRepository<PayoutInfo, Long> {
    @Modifying
    @Transactional
    @Query("DELETE FROM PayoutInfo p WHERE p.payroll.payrollId = :payrollId")
    void deleteByPayroll_PayrollId(@Param("payrollId") Integer payrollId);
}
