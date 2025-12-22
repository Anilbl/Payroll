package np.edu.nast.payroll.Payroll.service.impl;

import np.edu.nast.payroll.Payroll.entity.SystemConfig;
import np.edu.nast.payroll.Payroll.entity.User;
import np.edu.nast.payroll.Payroll.repository.SystemConfigRepository;
import np.edu.nast.payroll.Payroll.repository.UserRepository;
import np.edu.nast.payroll.Payroll.service.SystemConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SystemConfigServiceImpl implements SystemConfigService {

    @Autowired
    private SystemConfigRepository repository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public SystemConfig saveConfig(SystemConfig config) {

        // ---- NULL VALIDATION ----
        if (config.getKeyName() == null || config.getKeyName().isBlank()) {
            throw new RuntimeException("SystemConfig keyName must not be null or empty");
        }

        if (config.getValue() == null) {
            throw new RuntimeException("SystemConfig value must not be null");
        }

        if (config.getUpdatedBy() == null || config.getUpdatedBy().getUserId() == null) {
            throw new RuntimeException("updatedBy user must not be null");
        }

        // ---- FOREIGN KEY VALIDATION ----
        User user = userRepository.findById(config.getUpdatedBy().getUserId())
                .orElseThrow(() -> new RuntimeException("UpdatedBy user not found"));

        config.setUpdatedBy(user);

        // ---- HANDLE DUPLICATE KEY (UPDATE IF EXISTS) ----
        repository.findByKeyName(config.getKeyName()).ifPresent(existing -> {
            config.setConfigId(existing.getConfigId());
        });

        return repository.save(config);
    }

    @Override
    public List<SystemConfig> getAllConfigs() {
        return repository.findAll();
    }

    @Override
    public SystemConfig getConfigById(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("SystemConfig not found"));
    }

    @Override
    public void deleteConfig(Integer id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("SystemConfig not found");
        }
        repository.deleteById(id);
    }

    @Override
    public SystemConfig getConfigByKey(String keyName) {
        return repository.findByKeyName(keyName)
                .orElseThrow(() -> new RuntimeException("SystemConfig key not found"));
    }
}
