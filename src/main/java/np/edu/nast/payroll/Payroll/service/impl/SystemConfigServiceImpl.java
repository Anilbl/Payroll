package np.edu.nast.payroll.Payroll.service.impl;

import lombok.extern.slf4j.Slf4j;
import np.edu.nast.payroll.Payroll.entity.SystemConfig;
import np.edu.nast.payroll.Payroll.entity.User;
import np.edu.nast.payroll.Payroll.repository.SystemConfigRepository;
import np.edu.nast.payroll.Payroll.repository.UserRepository;
import np.edu.nast.payroll.Payroll.service.SystemConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class SystemConfigServiceImpl implements SystemConfigService {

    @Autowired
    private SystemConfigRepository repository;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public SystemConfig saveConfig(SystemConfig config) {
        // 1. Fetch current username from JWT context
        String username = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication().getName();

        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Current admin session not found."));

        // 2. Look for existing key to prevent MySQL Unique Constraint error
        Optional<SystemConfig> existing = repository.findByKeyName(config.getKeyName());

        SystemConfig configToSave;
        if (existing.isPresent()) {
            configToSave = existing.get();
            configToSave.setValue(config.getValue());
            configToSave.setDescription(config.getDescription());
        } else {
            configToSave = config;
        }

        configToSave.setUpdatedBy(currentUser);
        return repository.save(configToSave);
    }

    @Override
    public List<SystemConfig> getAllConfigs() {
        return repository.findAll();
    }

    @Override
    public SystemConfig getConfigById(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("SystemConfig not found with ID: " + id));
    }

    @Override
    @Transactional
    public void deleteConfig(Integer id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("SystemConfig not found");
        }
        repository.deleteById(id);
    }

    @Override
    public SystemConfig getConfigByKey(String keyName) {
        return repository.findByKeyName(keyName)
                .orElseThrow(() -> new RuntimeException("SystemConfig key not found: " + keyName));
    }
}