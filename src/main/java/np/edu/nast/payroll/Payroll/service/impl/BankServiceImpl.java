package np.edu.nast.payroll.Payroll.service.impl;

import np.edu.nast.payroll.Payroll.entity.Bank;
import np.edu.nast.payroll.Payroll.repository.BankRepository;
import np.edu.nast.payroll.Payroll.service.BankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class BankServiceImpl implements BankService {

    @Autowired
    private BankRepository bankRepository;

    @Override
    public Bank saveBank(Bank bank) {
        return bankRepository.save(bank);
    }

    @Override
    public List<Bank> saveAllBanks(List<Bank> banks) {
        return bankRepository.saveAll(banks);
    }

    @Override
    public Bank updateBank(Bank bank) {
        return bankRepository.save(bank);
    }

    @Override
    public void deleteBank(Integer bankId) {
        bankRepository.deleteById(bankId);
    }

    @Override
    public Bank getBankById(Integer bankId) {
        return bankRepository.findById(bankId).orElse(null);
    }

    @Override
    public List<Bank> getAllBanks() {
        return bankRepository.findAll();
    }
}
