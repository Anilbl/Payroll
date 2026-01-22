package np.edu.nast.payroll.Payroll.service;

import np.edu.nast.payroll.Payroll.entity.GlobalSetting;
import java.util.List;

public interface GlobalSettingService {
    List<GlobalSetting> getAllSettings();
    GlobalSetting updateSetting(String key, String value);
    String getValue(String key, String defaultValue);
}