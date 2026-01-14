package np.edu.nast.payroll.Payroll.service.impl;

import np.edu.nast.payroll.Payroll.entity.SalaryComponent;
import np.edu.nast.payroll.Payroll.entity.SalaryComponentType;
import np.edu.nast.payroll.Payroll.repository.SalaryComponentRepository;
import np.edu.nast.payroll.Payroll.repository.SalaryComponentTypeRepository;
import np.edu.nast.payroll.Payroll.service.SalaryComponentService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SalaryComponentServiceImpl implements SalaryComponentService {

    private final SalaryComponentRepository repo;
    private final SalaryComponentTypeRepository typeRepo;

    public SalaryComponentServiceImpl(SalaryComponentRepository repo,
                                      SalaryComponentTypeRepository typeRepo) {
        this.repo = repo;
        this.typeRepo = typeRepo;
    }

    @Override
    public SalaryComponent create(SalaryComponent component) {
        if (component.getComponentType() == null || component.getComponentType().getComponentTypeId() == null) {
            throw new RuntimeException("SalaryComponentType must not be null");
        }

        SalaryComponentType type = typeRepo.findById(component.getComponentType().getComponentTypeId())
                .orElseThrow(() -> new RuntimeException("SalaryComponentType not found"));
        component.setComponentType(type);

        return repo.save(component);
    }

    @Override
    public SalaryComponent update(Long id, SalaryComponent component) {
        SalaryComponent existing = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("SalaryComponent not found"));

        if (component.getComponentType() == null || component.getComponentType().getComponentTypeId() == null) {
            throw new RuntimeException("SalaryComponentType must not be null");
        }

        SalaryComponentType type = typeRepo.findById(component.getComponentType().getComponentTypeId())
                .orElseThrow(() -> new RuntimeException("SalaryComponentType not found"));

        existing.setComponentName(component.getComponentName());
        existing.setComponentType(type);
        existing.setCalculationMethod(component.getCalculationMethod());
        existing.setDefaultValue(component.getDefaultValue());
        existing.setDescription(component.getDescription());
        existing.setRequired(component.isRequired());

        return repo.save(existing);
    }

    @Override
    public void delete(Long id) {
        repo.deleteById(id);
    }

    @Override
    public SalaryComponent getById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("SalaryComponent not found"));
    }

    @Override
    public List<SalaryComponent> getAll() {
        return repo.findAll();
    }
}
