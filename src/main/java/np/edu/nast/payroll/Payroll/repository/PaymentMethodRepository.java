package np.edu.nast.payroll.Payroll.repository;
import np.edu.nast.payroll.Payroll.entity.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Integer> {
    // Custom query to find only active methods
    List<PaymentMethod> findByIsActiveTrue();
}
