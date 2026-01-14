package np.edu.nast.payroll.Payroll.service.impl;

import np.edu.nast.payroll.Payroll.entity.GradeSalaryComponent;
import np.edu.nast.payroll.Payroll.entity.SalaryComponent;
import np.edu.nast.payroll.Payroll.entity.SalaryGrade;
import np.edu.nast.payroll.Payroll.repository.GradeSalaryComponentRepository;
import np.edu.nast.payroll.Payroll.repository.SalaryComponentRepository;
import np.edu.nast.payroll.Payroll.repository.SalaryGradeRepository;
import np.edu.nast.payroll.Payroll.service.GradeSalaryComponentService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GradeSalaryComponentServiceImpl implements GradeSalaryComponentService {

    private final GradeSalaryComponentRepository repo;
    private final SalaryGradeRepository gradeRepo;
    private final SalaryComponentRepository componentRepo;

    public GradeSalaryComponentServiceImpl(
            GradeSalaryComponentRepository repo,
            SalaryGradeRepository gradeRepo,
            SalaryComponentRepository componentRepo) {
        this.repo = repo;
        this.gradeRepo = gradeRepo;
        this.componentRepo = componentRepo;
    }

    @Override
    public GradeSalaryComponent create(GradeSalaryComponent gsc) {
        // Validate foreign keys
        if (gsc.getGrade() == null || gsc.getGrade().getGradeId() == null) {
            throw new RuntimeException("SalaryGrade must not be null");
        }
        if (gsc.getComponent() == null || gsc.getComponent().getComponentId() == null) {
            throw new RuntimeException("SalaryComponent must not be null");
        }

        // Fetch full entities from DB
        SalaryGrade grade = gradeRepo.findById(gsc.getGrade().getGradeId())
                .orElseThrow(() -> new RuntimeException("SalaryGrade not found"));
        SalaryComponent component = componentRepo.findById(gsc.getComponent().getComponentId())
                .orElseThrow(() -> new RuntimeException("SalaryComponent not found"));

        gsc.setGrade(grade);
        gsc.setComponent(component);

        return repo.save(gsc);
    }

    @Override
    public GradeSalaryComponent update(Long id, GradeSalaryComponent gsc) {
        GradeSalaryComponent existing = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("GradeSalaryComponent not found"));

        // Validate foreign keys
        if (gsc.getGrade() == null || gsc.getGrade().getGradeId() == null) {
            throw new RuntimeException("SalaryGrade must not be null");
        }
        if (gsc.getComponent() == null || gsc.getComponent().getComponentId() == null) {
            throw new RuntimeException("SalaryComponent must not be null");
        }

        // Fetch full entities from DB
        SalaryGrade grade = gradeRepo.findById(gsc.getGrade().getGradeId())
                .orElseThrow(() -> new RuntimeException("SalaryGrade not found"));
        SalaryComponent component = componentRepo.findById(gsc.getComponent().getComponentId())
                .orElseThrow(() -> new RuntimeException("SalaryComponent not found"));

        existing.setGrade(grade);
        existing.setComponent(component);
        existing.setValue(gsc.getValue());

        return repo.save(existing);
    }

    @Override
    public void delete(Long id) {
        repo.deleteById(id);
    }

    @Override
    public GradeSalaryComponent getById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("GradeSalaryComponent not found"));
    }

    @Override
    public List<GradeSalaryComponent> getAll() {
        return repo.findAll();
    }
}
