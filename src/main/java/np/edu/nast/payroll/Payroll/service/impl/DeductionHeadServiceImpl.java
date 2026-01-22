package np.edu.nast.payroll.Payroll.service.impl;

import np.edu.nast.payroll.Payroll.entity.DeductionHead;
import np.edu.nast.payroll.Payroll.exception.ResourceNotFoundException;
import np.edu.nast.payroll.Payroll.repository.DeductionHeadRepository;
import np.edu.nast.payroll.Payroll.service.DeductionHeadService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class DeductionHeadServiceImpl implements DeductionHeadService {

    private final DeductionHeadRepository repository;

    public DeductionHeadServiceImpl(DeductionHeadRepository repository) {
        this.repository = repository;
    }

    @Override
    public DeductionHead saveDeductionHead(DeductionHead head) {
        if (head == null) {
            throw new IllegalArgumentException("DeductionHead cannot be null");
        }

        // --- UPDATE LOGIC ---
        // If an ID exists, we are editing an existing record
        if (head.getDeductionHeadId() != null) {
            DeductionHead existing = repository.findById(head.getDeductionHeadId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "DeductionHead not found with ID: " + head.getDeductionHeadId()));

            // Update the fields from the incoming 'head' object
            existing.setName(head.getName());
            existing.setDescription(head.getDescription());

            // If your entity has these fields, uncomment them:
            // existing.setAmount(head.getAmount());
            // existing.setIsPercentage(head.getIsPercentage());

            return repository.save(existing);
        }

        // --- CREATE LOGIC ---
        // If ID is null, this is a new record. Check for name duplicates first.
        repository.findByName(head.getName()).ifPresent(d -> {
            throw new RuntimeException("Deduction Head with name '" + head.getName() + "' already exists.");
        });

        return repository.save(head);
    }

    @Override
    public List<DeductionHead> getAllDeductionHeads() {
        return repository.findAll();
    }

    @Override
    public DeductionHead getDeductionHeadById(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "DeductionHead not found with ID: " + id));
    }

    @Override
    public DeductionHead updateDeductionHead(DeductionHead head) {
        // This method can now just call our smart saveDeductionHead logic
        return saveDeductionHead(head);
    }

    @Override
    public void deleteDeductionHead(Integer id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("DeductionHead not found with ID: " + id);
        }
        repository.deleteById(id);
    }
}