package np.edu.nast.payroll.Payroll.repository;

import np.edu.nast.payroll.Payroll.entity.GlobalSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface GlobalSettingRepository extends JpaRepository<GlobalSetting, Integer> {
    Optional<GlobalSetting> findBySettingKey(String key);
}