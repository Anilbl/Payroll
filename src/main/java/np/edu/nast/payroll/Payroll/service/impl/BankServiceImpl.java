package np.edu.nast.payroll.Payroll.service.impl;

import np.edu.nast.payroll.Payroll.entity.Bank;
import np.edu.nast.payroll.Payroll.exception.ResourceNotFoundException;
import np.edu.nast.payroll.Payroll.repository.BankRepository;
import np.edu.nast.payroll.Payroll.service.BankService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class BankServiceImpl implements BankService {

    private final BankRepository bankRepository;

    public BankServiceImpl(BankRepository bankRepository) {
        this.bankRepository = bankRepository;
    }

    /**
     * This method handles both Creating a new bank and Updating an existing one.
     * It ensures all mandatory fields are mapped correctly to the snake_case columns.
     */
    @Override
    public Bank saveBank(Bank bank) {
        if (bank == null) {
            throw new IllegalArgumentException("Bank data cannot be null");
        }

        // --- CASE 1: UPDATE EXISTING BANK ---
        if (bank.getBankId() != null) {
            Bank existing = bankRepository.findById(bank.getBankId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Bank not found with ID: " + bank.getBankId()));

            // Explicitly set fields to ensure they match the persistent entity
            existing.setBankName(bank.getBankName());
            existing.setBranchName(bank.getBranchName());
            existing.setBranchCode(bank.getBranchCode());
            existing.setAddress(bank.getAddress());
            existing.setContactNumber(bank.getContactNumber());

            return bankRepository.save(existing);
        }

        // --- CASE 2: SAVE NEW BANK ---
        // Basic validation before insert to prevent DB constraint errors
        if (bank.getBankName() == null || bank.getBankName().isBlank()) {
            throw new IllegalArgumentException("Bank Name is required.");
        }

        return bankRepository.save(bank);
    }

    @Override
    public List<Bank> saveAllBanks(List<Bank> banks) {
        if (banks == null || banks.isEmpty()) {
            throw new IllegalArgumentException("Bank list must not be empty");
        }
        return bankRepository.saveAll(banks);
    }

    @Override
    public Bank updateBank(Bank bank) {
        // We reuse saveBank logic to maintain consistency
        return saveBank(bank);
    }

    @Override
    public void deleteBank(Integer bankId) {
        if (!bankRepository.existsById(bankId)) {
            throw new ResourceNotFoundException("Bank not found with ID: " + bankId);
        }
        bankRepository.deleteById(bankId);
    }

    @Override
    public Bank getBankById(Integer bankId) {
        return bankRepository.findById(bankId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Bank not found with ID: " + bankId));
    }

    @Override
    public List<Bank> getAllBanks() {
        return bankRepository.findAll();
    }
}