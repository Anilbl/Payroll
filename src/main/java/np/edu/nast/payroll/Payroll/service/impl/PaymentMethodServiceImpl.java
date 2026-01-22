package np.edu.nast.payroll.Payroll.service.impl;

import np.edu.nast.payroll.Payroll.entity.PaymentMethod;
import np.edu.nast.payroll.Payroll.repository.PaymentMethodRepository;
import np.edu.nast.payroll.Payroll.service.PaymentMethodService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PaymentMethodServiceImpl implements PaymentMethodService {

    private final PaymentMethodRepository repo;

    public PaymentMethodServiceImpl(PaymentMethodRepository repo) {
        this.repo = repo;
    }

    @Override
    public PaymentMethod create(PaymentMethod p) {
        if (p.getMethodName() == null || p.getMethodName().isBlank()) {
            throw new RuntimeException("Method Name is required");
        }
        return repo.save(p);
    }

    @Override
    public PaymentMethod update(Integer id, PaymentMethod p) {
        PaymentMethod ex = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("PaymentMethod not found with ID: " + id));

        // Update fields explicitly
        ex.setMethodName(p.getMethodName());
        ex.setDetails(p.getDetails());

        return repo.save(ex);
    }

    @Override
    public void delete(Integer id) {
        PaymentMethod method = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("PaymentMethod not found"));

        // Instead of repo.deleteById(id), we do:
        method.setIsActive(false);
        repo.save(method);
    }

    @Override
    public PaymentMethod getById(Integer id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("PaymentMethod not found"));
    }

    @Override
    public List<PaymentMethod> getAll() {
        // Change repo.findAll() to repo.findByIsActiveTrue()
        return repo.findByIsActiveTrue();
    }
}