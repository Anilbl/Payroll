package np.edu.nast.payroll.Payroll.service.impl;

import np.edu.nast.payroll.Payroll.entity.*;
import np.edu.nast.payroll.Payroll.repository.*;
import np.edu.nast.payroll.Payroll.service.BankAccountService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class BankAccountServiceImpl implements BankAccountService {

    private final BankAccountRepository repo;
    private final EmployeeRepository employeeRepo;
    private final BankRepository bankRepo;

    public BankAccountServiceImpl(
            BankAccountRepository repo,
            EmployeeRepository employeeRepo,
            BankRepository bankRepo
    ) {
        this.repo = repo;
        this.employeeRepo = employeeRepo;
        this.bankRepo = bankRepo;
    }

    @Override
    public BankAccount create(BankAccount account) {

        // 1. Validate mandatory foreign keys
        if (account.getEmployee() == null || account.getEmployee().getEmpId() == null) {
            throw new RuntimeException("Employee ID must not be null");
        }

        if (account.getBank() == null || account.getBank().getBankId() == null) {
            throw new RuntimeException("Bank ID must not be null");
        }

        // 2. Load managed Employee (DB check)
        Employee employee = employeeRepo.findById(account.getEmployee().getEmpId())
                .orElseThrow(() -> new RuntimeException("Employee does not exist"));

        // 3. Load managed Bank (DB check)
        Bank bank = bankRepo.findById(account.getBank().getBankId())
                .orElseThrow(() -> new RuntimeException("Bank does not exist"));

        // 4. Replace detached objects with managed ones
        account.setEmployee(employee);
        account.setBank(bank);

        // 5. Optional business rule:
        // Only ONE primary account per employee
        if (Boolean.TRUE.equals(account.getIsPrimary())) {
            repo.findByEmployeeEmpId(employee.getEmpId())
                    .forEach(existing -> {
                        existing.setIsPrimary(false);
                        repo.save(existing);
                    });
        }

        return repo.save(account);
    }

    @Override
    public BankAccount update(Integer id, BankAccount account) {

        BankAccount existing = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("BankAccount not found"));

        // Update only allowed fields
        existing.setAccountNumber(account.getAccountNumber());
        existing.setAccountType(account.getAccountType());
        existing.setCurrency(account.getCurrency());
        existing.setIsPrimary(account.getIsPrimary());

        return repo.save(existing);
    }

    @Override
    public void delete(Integer id) {
        repo.deleteById(id);
    }

    @Override
    public BankAccount getById(Integer id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("BankAccount not found"));
    }

    @Override
    public List<BankAccount> getAll() {
        return repo.findAll();
    }

    @Override
    public List<BankAccount> findByEmployeeId(Integer empId) {
        return repo.findByEmployeeEmpId(empId);
    }
}
