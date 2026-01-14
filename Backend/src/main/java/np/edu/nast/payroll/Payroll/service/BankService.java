package np.edu.nast.payroll.Payroll.service;

import np.edu.nast.payroll.Payroll.entity.Bank;
import java.util.List;

public interface BankService {

    Bank saveBank(Bank bank);

    List<Bank> saveAllBanks(List<Bank> banks);

    Bank updateBank(Bank bank);

    void deleteBank(Integer bankId);

    Bank getBankById(Integer bankId);

    List<Bank> getAllBanks();
}
