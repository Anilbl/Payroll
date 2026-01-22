package np.edu.nast.payroll.Payroll.service.impl;

import lombok.RequiredArgsConstructor;
import np.edu.nast.payroll.Payroll.entity.GlobalSetting;
import np.edu.nast.payroll.Payroll.repository.GlobalSettingRepository;
import np.edu.nast.payroll.Payroll.service.GlobalSettingService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GlobalSettingServiceImpl implements GlobalSettingService {

    private final GlobalSettingRepository repository;

    @Override
    public List<GlobalSetting> getAllSettings() {
        return repository.findAll();
    }

    @Override
    public GlobalSetting updateSetting(String key, String value) {
        GlobalSetting setting = repository.findBySettingKey(key)
                .orElseThrow(() -> new RuntimeException("Setting not found: " + key));
        setting.setSettingValue(value);
        return repository.save(setting);
    }

    @Override
    public String getValue(String key, String defaultValue) {
        return repository.findBySettingKey(key)
                .map(GlobalSetting::getSettingValue)
                .orElse(defaultValue);
    }
}