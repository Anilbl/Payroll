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

        // 1. Basic Validation
        if (config.getKeyName() == null || config.getKeyName().isBlank()) {
            throw new RuntimeException("SystemConfig keyName must not be null or empty");
        }

        if (config.getUpdatedBy() == null || config.getUpdatedBy().getUserId() == null) {
            throw new RuntimeException("updatedBy user must not be null");
        }

        // 2. Foreign Key Validation
        User user = userRepository.findById(config.getUpdatedBy().getUserId())
                .orElseThrow(() -> new RuntimeException("UpdatedBy user not found with ID: " + config.getUpdatedBy().getUserId()));

        config.setUpdatedBy(user);

        // 3. Update vs Create Logic
        if (config.getConfigId() != null) {
            // Logic for UPDATE
            SystemConfig existing = repository.findById(config.getConfigId())
                    .orElseThrow(() -> new RuntimeException("Configuration not found with ID: " + config.getConfigId()));

            existing.setValue(config.getValue());
            existing.setDescription(config.getDescription());
            existing.setUpdatedBy(user);
            // keyName is usually not changed during update to maintain system integrity
            return repository.save(existing);
        } else {
            // Logic for NEW Record
            repository.findByKeyName(config.getKeyName()).ifPresent(existing -> {
                throw new RuntimeException("A configuration with key '" + config.getKeyName() + "' already exists.");
            });
            return repository.save(config);
        }
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