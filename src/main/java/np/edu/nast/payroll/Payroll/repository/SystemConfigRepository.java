package np.edu.nast.payroll.Payroll.repository;

import np.edu.nast.payroll.Payroll.entity.SystemConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SystemConfigRepository extends JpaRepository<SystemConfig, Integer> {
    Optional<SystemConfig> findByKeyName(String keyName);
}
