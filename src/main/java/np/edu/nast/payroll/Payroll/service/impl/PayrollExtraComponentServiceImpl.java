package np.edu.nast.payroll.Payroll.service.impl;

import lombok.RequiredArgsConstructor;
import np.edu.nast.payroll.Payroll.entity.PayrollExtraComponent;
import np.edu.nast.payroll.Payroll.repository.PayrollExtraComponentRepository;
import np.edu.nast.payroll.Payroll.service.PayrollExtraComponentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PayrollExtraComponentServiceImpl implements PayrollExtraComponentService {

    private final PayrollExtraComponentRepository componentRepo;

    @Override
    public Optional<PayrollExtraComponent> getById(Long id) {
        return componentRepo.findById(id);
    }
    @Override
    public List<PayrollExtraComponent> getComponentsByPayrollId(Integer payrollId) {
        return componentRepo.findByPayroll_PayrollId(payrollId);
    }

    @Override
    @Transactional
    public void deleteComponent(Long id) {
        componentRepo.deleteById(id);
    }
}