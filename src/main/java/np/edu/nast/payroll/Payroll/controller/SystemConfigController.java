package np.edu.nast.payroll.Payroll.controller;

import np.edu.nast.payroll.Payroll.entity.SystemConfig;
import np.edu.nast.payroll.Payroll.service.SystemConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/system-config")
@CrossOrigin(origins = "http://localhost:5173")
public class SystemConfigController {

    @Autowired
    private SystemConfigService service;

    @PostMapping
    public ResponseEntity<?> saveOrUpdate(@RequestBody SystemConfig config) {
        try {
            SystemConfig saved = service.saveConfig(config);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Operation failed: " + e.getMessage()));
        }
    }

    @GetMapping
    public List<SystemConfig> getAllConfigs() {
        return service.getAllConfigs();
    }

    @GetMapping("/{id}")
    public ResponseEntity<SystemConfig> getConfig(@PathVariable Integer id) {
        return ResponseEntity.ok(service.getConfigById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteConfig(@PathVariable Integer id) {
        try {
            service.deleteConfig(id);
            return ResponseEntity.ok(Map.of("message", "Configuration deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(404).body(Map.of("message", e.getMessage()));
        }
    }
}